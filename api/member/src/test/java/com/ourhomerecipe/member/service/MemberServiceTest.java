package com.ourhomerecipe.member.service;
import static com.ourhomerecipe.domain.common.error.code.MemberErrorCode.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import com.ourhomerecipe.domain.member.Member;
import com.ourhomerecipe.domain.member.repository.MemberRepository;
import com.ourhomerecipe.dto.member.request.MemberRegisterRequestDto;
import com.ourhomerecipe.member.exception.MemberException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * MemberService의 단위 테스트를 위한 테스트 클래스.
 *
 * 이 클래스는 회원 가입 기능에 대한 다양한 시나리오를 테스트:
 * - 정상적인 회원 가입
 * - 이미 존재하는 이메일로 가입 시도
 * - 비밀번호 불일치
 * - 이미 존재하는 전화번호로 가입 시도
 * - 이미 존재하는 닉네임으로 가입 시도
 *
 * 각 테스트 케이스는 Given-When-Then 구조를 따르며,
 * Mockito를 사용하여 MemberRepository와 PasswordEncoder의 동작을 모의(Mock).
 */
@ExtendWith(MockitoExtension.class)  // MockitoExtension을 사용하여 Mock 객체 자동 초기화
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;  // MemberRepository를 Mock으로 선언하여 테스트 시 가짜로 동작

    @Mock
    private PasswordEncoder passwordEncoder;    // PasswordEncoder를 Mock으로 선언

    @InjectMocks
    private MemberService memberService;        // MemberService에 Mock 객체 주입


    @Test
    @DisplayName("회원 가입 성공 테스트")  // 테스트 이름 설정
    void registerMember_Success() {
        // Given: 유효한 회원가입 요청 데이터 준비
        MemberRegisterRequestDto requestDto = new MemberRegisterRequestDto();
        requestDto.setEmail("test@example.com");
        requestDto.setPassword("password123");
        requestDto.setPasswordConfirm("password123");
        requestDto.setPhoneNumber("01012345678");
        requestDto.setNickname("testUser");

        // Mock 설정: 중복 체크 시 모두 false 반환, 비밀번호 인코딩, save 호출 시 객체 반환
        when(memberRepository.existsByEmail(anyString())).thenReturn(false);
        when(memberRepository.existsByPhoneNumber(anyString())).thenReturn(false);
        when(memberRepository.existsByNickname(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // 저장된 객체를 그대로 반환하도록 설정
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));


        // When: 회원가입 서비스 호출
        Member result = memberService.registerMember(requestDto);

        // Then: 결과 검증 - 반환된 Member 객체가 null이 아닌지 확인
        assertNotNull(result);
        // save 메소드가 호출되었는지 확인
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    @DisplayName("이메일 중복으로 인한 회원가입 실패 테스트")  // 이메일 중복으로 인한 실패를 검증
    void registerMember_EmailAlreadyExists() {
        // Given: 이미 존재하는 이메일로 회원가입 요청
        MemberRegisterRequestDto requestDto = new MemberRegisterRequestDto();
        requestDto.setEmail("existing@example.com");

        // 이메일 중복 체크 시 true 반환 (이미 존재함)
        when(memberRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // When & Then: 이메일 중복으로 예외 발생 여부 검증
        MemberException exception = assertThrows(MemberException.class,
                () -> memberService.registerMember(requestDto));

        // 발생한 예외의 에러 코드가 EXISTS_MEMBER_EMAIL인지 확인
        assertEquals(EXISTS_MEMBER_EMAIL, exception.getErrorCode());
    }

    @Test
    @DisplayName("비밀번호 불일치로 인한 회원가입 실패 테스트")  // 비밀번호 불일치를 검증
    void registerMember_PasswordMismatch() {
        // Given: 비밀번호와 확인 비밀번호가 일치하지 않는 요청 데이터
        MemberRegisterRequestDto requestDto = new MemberRegisterRequestDto();
        requestDto.setPassword("password123");
        requestDto.setPasswordConfirm("password456");

        // When & Then: 비밀번호 불일치로 인한 예외 발생 여부 검증
        MemberException exception = assertThrows(MemberException.class,
                () -> memberService.registerMember(requestDto));

        // 발생한 예외의 에러 코드가 NOT_MATCHED_PASSWORD인지 확인
        assertEquals(NOT_MATCHED_PASSWORD, exception.getErrorCode());
    }

    @Test
    @DisplayName("전화번호 중복으로 인한 회원가입 실패 테스트")  // 전화번호 중복 검증
    void registerMember_PhoneNumberAlreadyExists() {
        // Given: 이미 존재하는 전화번호로 회원가입 요청
        MemberRegisterRequestDto requestDto = new MemberRegisterRequestDto();
        requestDto.setEmail("test@example.com");
        requestDto.setPassword("password123");
        requestDto.setPasswordConfirm("password123");
        requestDto.setPhoneNumber("01012345678");

        // Mock 설정: 이메일 중복은 없지만 전화번호 중복이 있음
        when(memberRepository.existsByEmail(anyString())).thenReturn(false);
        when(memberRepository.existsByPhoneNumber("01012345678")).thenReturn(true);

        // When & Then: 전화번호 중복으로 예외 발생 여부 검증
        MemberException exception = assertThrows(MemberException.class,
                () -> memberService.registerMember(requestDto));

        // 발생한 예외의 에러 코드가 EXISTS_MEMBER_PHONE_NUMBER인지 확인
        assertEquals(EXISTS_MEMBER_PHONE_NUMBER, exception.getErrorCode());
    }

    @Test
    @DisplayName("닉네임 중복으로 인한 회원가입 실패 테스트")  // 닉네임 중복 검증
    void registerMember_NicknameAlreadyExists() {
        // Given: 이미 존재하는 닉네임으로 회원가입 요청
        MemberRegisterRequestDto requestDto = new MemberRegisterRequestDto();
        requestDto.setEmail("test@example.com");
        requestDto.setPassword("password123");
        requestDto.setPasswordConfirm("password123");
        requestDto.setPhoneNumber("01012345678");
        requestDto.setNickname("existingNickname");

        // Mock 설정: 이메일, 전화번호 중복 없음, 닉네임 중복 있음
        when(memberRepository.existsByEmail(anyString())).thenReturn(false);
        when(memberRepository.existsByPhoneNumber(anyString())).thenReturn(false);
        when(memberRepository.existsByNickname("existingNickname")).thenReturn(true);

        // When & Then: 닉네임 중복으로 예외 발생 여부 검증
        MemberException exception = assertThrows(MemberException.class,
                () -> memberService.registerMember(requestDto));

        // 발생한 예외의 에러 코드가 EXISTS_MEMBER_NICKNAME인지 확인
        assertEquals(EXISTS_MEMBER_NICKNAME, exception.getErrorCode());
    }
}