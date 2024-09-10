package com.ourhomerecipe.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ourhomerecipe.domain.member.Member;
import com.ourhomerecipe.domain.member.repository.MemberRepository;
import com.ourhomerecipe.dto.member.request.MemberRegisterRequestDto;
import com.ourhomerecipe.infra.config.QuerydslConfig;
import com.ourhomerecipe.member.exception.MemberException;
import com.ourhomerecipe.member.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.ourhomerecipe.domain.common.code.GlobalSuccessCode.CREATE;
import static com.ourhomerecipe.domain.common.error.code.MemberErrorCode.EXISTS_MEMBER_EMAIL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
@ActiveProfiles("test")  // test 프로필을 사용

class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private QuerydslConfig querydslConfig;  // Querydsl 설정을 Mock 처리

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerMember_Success() throws Exception {
        // Given
        MemberRegisterRequestDto requestDto = new MemberRegisterRequestDto();
        requestDto.setEmail("test@example.com");
        requestDto.setPassword("password123");
        requestDto.setPasswordConfirm("password123");
        requestDto.setPhoneNumber("01012345678");
        requestDto.setNickname("testUser");

        Member mockMember = new Member();
        mockMember.setId(1L);

        when(memberService.registerMember(any(MemberRegisterRequestDto.class))).thenReturn(mockMember);

        // When
        ResultActions resultActions = mockMvc.perform(post("/member/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // Then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(CREATE.getStatus()))
                .andExpect(jsonPath("$.data.id").value(1L));

        verify(memberService).registerMember(any(MemberRegisterRequestDto.class));
    }

    @Test
    void registerMember_InvalidInput() throws Exception {
        // Given
        MemberRegisterRequestDto requestDto = new MemberRegisterRequestDto();
        // 이메일을 비워두어 유효성 검사 실패를 유도
        requestDto.setPassword("password123");
        requestDto.setPasswordConfirm("password123");
        requestDto.setPhoneNumber("01012345678");
        requestDto.setNickname("testUser");

        // When
        ResultActions resultActions = mockMvc.perform(post("/member/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // Then
        resultActions
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerMember_DuplicateEmail() throws Exception {
        // Given
        MemberRegisterRequestDto requestDto = new MemberRegisterRequestDto();
        requestDto.setEmail("existing@example.com");
        requestDto.setPassword("password123");
        requestDto.setPasswordConfirm("password123");
        requestDto.setPhoneNumber("01012345678");
        requestDto.setNickname("testUser");

        when(memberService.registerMember(any(MemberRegisterRequestDto.class)))
                .thenThrow(new MemberException(EXISTS_MEMBER_EMAIL));

        // When
        ResultActions resultActions = mockMvc.perform(post("/member/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // Then
        resultActions
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(EXISTS_MEMBER_EMAIL.getStatus()))
                .andExpect(jsonPath("$.message").value(EXISTS_MEMBER_EMAIL.getErrorMessage()));
    }
}