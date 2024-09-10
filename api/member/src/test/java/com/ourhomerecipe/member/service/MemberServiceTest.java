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

public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        // 테스트 시작 전 Mock 객체 초기화
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerMember_Success() {
        // Given: 유효한 회원가입
        MemberRegisterRequestDto requestDto = new MemberRegisterRequestDto();
        requestDto.setEmail("test@example.com");
        requestDto.setPassword("password123");
        requestDto.setPasswordConfirm("password123");
        requestDto.setPhoneNumber("01012345678");
        requestDto.setNickname("testUser");

        when(memberRepository.existsByEmail(anyString())).thenReturn(false);
        when(memberRepository.existsByPhoneNumber(anyString())).thenReturn(false);
        when(memberRepository.existsByNickname(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(memberRepository.save(any(Member.class))).thenReturn(new Member());

        // When: 회원가입 서비스 메소드 호출
        Member result = memberService.registerMember(requestDto);

        // Then: 결과 검증
        assertNotNull(result);
        verify(memberRepository).save(any(Member.class));
    }


    @Test
    void registerMember_EmailAlreadyExists() {
        // Given: 이미 존재하는 이메일로 회원가입 요청
        MemberRegisterRequestDto requestDto = new MemberRegisterRequestDto();
        requestDto.setEmail("existing@example.com");

        when(memberRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // When: 회원가입 서비스 메소드 호출
        MemberException exception = assertThrows(MemberException.class,
                () -> memberService.registerMember(requestDto));

        // Then: 예외 타입과 에러 코드 검증
        assertEquals(EXISTS_MEMBER_EMAIL, exception.getErrorCode());
    }

    @Test
    void registerMember_PasswordMismatch() {
        // Given: 비밀번호와 확인 비밀번호가 일치하지 않는 요청
        MemberRegisterRequestDto requestDto = new MemberRegisterRequestDto();
        requestDto.setPassword("password123");
        requestDto.setPasswordConfirm("password456");

        // When: 회원가입 서비스 메소드 호출
        MemberException exception = assertThrows(MemberException.class,
                () -> memberService.registerMember(requestDto));

        // Then: 예외 타입과 에러 코드 검증
        assertEquals(NOT_MATCHED_PASSWORD, exception.getErrorCode());
    }

    @Test
    void registerMember_PhoneNumberAlreadyExists() {
        // Given: 이미 존재하는 전화번호로 회원가입 요청
        MemberRegisterRequestDto requestDto = new MemberRegisterRequestDto();
        requestDto.setEmail("test@example.com");
        requestDto.setPassword("password123");
        requestDto.setPasswordConfirm("password123");
        requestDto.setPhoneNumber("01012345678");

        when(memberRepository.existsByEmail(anyString())).thenReturn(false);
        when(memberRepository.existsByPhoneNumber("01012345678")).thenReturn(true);

        // When: 회원가입 서비스 메소드 호출
        MemberException exception = assertThrows(MemberException.class,
                () -> memberService.registerMember(requestDto));

        // Then: 예외 타입과 에러 코드 검증
        assertEquals(EXISTS_MEMBER_PHONE_NUMBER, exception.getErrorCode());
    }

    @Test
    void registerMember_NicknameAlreadyExists() {

        // Given: 이미 존재하는 닉네임으로 회원가입 요청
        MemberRegisterRequestDto requestDto = new MemberRegisterRequestDto();
        requestDto.setEmail("test@example.com");
        requestDto.setPassword("password123");
        requestDto.setPasswordConfirm("password123");
        requestDto.setPhoneNumber("01012345678");
        requestDto.setNickname("existingNickname");

        when(memberRepository.existsByEmail(anyString())).thenReturn(false);
        when(memberRepository.existsByPhoneNumber(anyString())).thenReturn(false);
        when(memberRepository.existsByNickname("existingNickname")).thenReturn(true);

        // When: 회원가입 서비스 메소드 호출
        MemberException exception = assertThrows(MemberException.class,
                () -> memberService.registerMember(requestDto));

        // Then: 예외 타입과 에러 코드 검증
        assertEquals(EXISTS_MEMBER_NICKNAME, exception.getErrorCode());
    }
}