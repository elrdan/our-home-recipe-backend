package com.ourhomerecipe.member.service;

import static com.ourhomerecipe.domain.common.error.code.MemberErrorCode.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.ourhomerecipe.domain.member.Member;
import com.ourhomerecipe.domain.member.repository.MemberRepository;
import com.ourhomerecipe.dto.member.request.MemberRegisterRequestDto;
import com.ourhomerecipe.member.exception.MemberException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
 *
 * @see MemberService
 * @see MemberRepository
 * @see PasswordEncoder
 */
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        // 각 테스트 메소드 실행 전에 Mock 객체들을 초기화
        // 이를 통해 각 테스트가 독립적으로 실행
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerMember_Success() {
        // Given: 유효한 회원가입 요청 데이터 준비
        MemberRegisterRequestDto requestDto = new MemberRegisterRequestDto();
        requestDto.setEmail("test@example.com");
        requestDto.setPassword("password123");
        requestDto.setPasswordConfirm("password123");
        requestDto.setPhoneNumber("01012345678");
        requestDto.setNickname("testUser");

        // Mock 객체의 동작 정의
        // 1. 이메일, 전화번호, 닉네임 중복 체크 시 모두 false 반환 (중복 없음)
        when(memberRepository.existsByEmail(anyString())).thenReturn(false);
        when(memberRepository.existsByPhoneNumber(anyString())).thenReturn(false);
        when(memberRepository.existsByNickname(anyString())).thenReturn(false);

        // 2. 비밀번호 인코딩 시 "encodedPassword" 반환
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // 3. 회원 저장 시 새로운 Member 객체 반환
        when(memberRepository.save(any(Member.class))).thenReturn(new Member());

        // When: 회원가입 서비스 메소드 호출
        Member result = memberService.registerMember(requestDto);

        // Then: 결과 검증
        // 1. 반환된 Member 객체가 null이 아닌지 확인
        assertNotNull(result);
        // 2. memberRepository의 save 메소드가 실제로 호출되었는지 확인
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    void registerMember_EmailAlreadyExists() {
        // Given: 이미 존재하는 이메일로 회원가입 요청
        MemberRegisterRequestDto requestDto = new MemberRegisterRequestDto();
        requestDto.setEmail("existing@example.com");

        // 이메일 중복 체크 시 true 반환 (이미 존재함)
        when(memberRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // When & Then: 회원가입 서비스 메소드 호출 시 예외 발생 검증
        MemberException exception = assertThrows(MemberException.class,
                () -> memberService.registerMember(requestDto));

        // 발생한 예외의 에러 코드가 EXISTS_MEMBER_EMAIL인지 확인
        assertEquals(EXISTS_MEMBER_EMAIL, exception.getErrorCode());
    }

    @Test
    void registerMember_PasswordMismatch() {
        // Given: 비밀번호와 확인 비밀번호가 일치하지 않는 요청 데이터 준비
        MemberRegisterRequestDto requestDto = new MemberRegisterRequestDto();
        requestDto.setPassword("password123");
        requestDto.setPasswordConfirm("password456");

        // When & Then: 회원가입 서비스 메소드 호출 시 예외 발생 검증
        MemberException exception = assertThrows(MemberException.class,
                () -> memberService.registerMember(requestDto));

        // 발생한 예외의 에러 코드가 NOT_MATCHED_PASSWORD인지 확인
        assertEquals(NOT_MATCHED_PASSWORD, exception.getErrorCode());
    }

    @Test
    void registerMember_PhoneNumberAlreadyExists() {
        // Given: 이미 존재하는 전화번호로 회원가입 요청 데이터 준비
        MemberRegisterRequestDto requestDto = new MemberRegisterRequestDto();
        requestDto.setEmail("test@example.com");
        requestDto.setPassword("password123");
        requestDto.setPasswordConfirm("password123");
        requestDto.setPhoneNumber("01012345678");

        // Mock 객체 동작 정의
        // 1. 이메일 중복 체크 시 false 반환 (중복 없음)
        when(memberRepository.existsByEmail(anyString())).thenReturn(false);

        // 2. 전화번호 중복 체크 시 true 반환 (이미 존재함)
        when(memberRepository.existsByPhoneNumber("01012345678")).thenReturn(true);

        // When & Then: 회원가입 서비스 메소드 호출 시 예외 발생 검증
        MemberException exception = assertThrows(MemberException.class,
                () -> memberService.registerMember(requestDto));

        // 발생한 예외의 에러 코드가 EXISTS_MEMBER_PHONE_NUMBER인지 확인
        assertEquals(EXISTS_MEMBER_PHONE_NUMBER, exception.getErrorCode());
    }

    @Test
    void registerMember_NicknameAlreadyExists() {
        // Given: 이미 존재하는 닉네임으로 회원가입 요청 데이터 준비
        MemberRegisterRequestDto requestDto = new MemberRegisterRequestDto();
        requestDto.setEmail("test@example.com");
        requestDto.setPassword("password123");
        requestDto.setPasswordConfirm("password123");
        requestDto.setPhoneNumber("01012345678");
        requestDto.setNickname("existingNickname");

        // Mock 객체 동작 정의
        // 1. 이메일 중복 체크 시 false 반환 (중복 없음)
        when(memberRepository.existsByEmail(anyString())).thenReturn(false);

        // 2. 전화번호 중복 체크 시 false 반환 (중복 없음)
        when(memberRepository.existsByPhoneNumber(anyString())).thenReturn(false);

        // 3. 닉네임 중복 체크 시 true 반환 (이미 존재함)
        when(memberRepository.existsByNickname("existingNickname")).thenReturn(true);

        // When & Then: 회원가입 서비스 메소드 호출 시 예외 발생 검증
        MemberException exception = assertThrows(MemberException.class,
                () -> memberService.registerMember(requestDto));

        // 발생한 예외의 에러 코드가 EXISTS_MEMBER_NICKNAME인지 확인
        assertEquals(EXISTS_MEMBER_NICKNAME, exception.getErrorCode());
    }
}