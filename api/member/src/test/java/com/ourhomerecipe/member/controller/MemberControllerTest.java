package com.ourhomerecipe.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ourhomerecipe.domain.member.Member;
import com.ourhomerecipe.dto.member.request.MemberRegisterRequestDto;
import com.ourhomerecipe.member.exception.MemberException;
import com.ourhomerecipe.member.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static com.ourhomerecipe.domain.common.error.code.MemberErrorCode.EXISTS_MEMBER_EMAIL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * MemberController의 통합 테스트를 위한 테스트 클래스
 *
 * 이 클래스는 회원 가입 API에 대한 다양한 시나리오를 테스트:
 * - 정상적인 회원 가입 (성공)
 * - 유효하지 않은 입력으로 인한 회원 가입 (실패)
 * - 이미 존재하는 이메일로 인한 회원 가입 (실패)
 *
 * 각 테스트 케이스는 Given-When-Then 구조를 따르며,
 * MockMvc를 사용하여 실제 HTTP 요청을 시뮬레이션
 * MemberService는 MockBean으로 처리되어 실제 서비스 계층을 호출하지 않고 테스트.
 *
 * @see MemberController
 * @see MemberService
 * @see MockMvc
 */
@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerTest {

    // MockMvc를 사용하여 실제 HTTP 요청을 보내지 않고도 컨트롤러 테스트를 수행
    @Autowired
    private MockMvc mockMvc;

    // MemberService를 Mock으로 처리하여 실제 서비스 계층을 호출하지 않고도 테스트할 수 있게 설정
    @MockBean
    private MemberService memberService;

    // JSON 처리를 위한 ObjectMapper를 주입합니다. 이 객체를 사용하여 요청 본문을 JSON으로 변환
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerMember_Success() throws Exception {
        // Given: 회원 가입 요청에 필요한 데이터를 준비
        MemberRegisterRequestDto requestDto = new MemberRegisterRequestDto();
        requestDto.setName("테스트");
        requestDto.setEmail("test@example.com");
        requestDto.setPassword("password123!!");
        requestDto.setPasswordConfirm("password123!!");
        requestDto.setPhoneNumber("01012345678");
        requestDto.setNickname("testUser");

        // 회원 등록 서비스가 정상적으로 수행되면, id가 1인 회원을 반환하도록 Mock 설정
        Member mockMember = new Member();
        mockMember.setId(1L);

        when(memberService.registerMember(any(MemberRegisterRequestDto.class))).thenReturn(mockMember);

        // When: MockMvc를 사용하여 /member/register로 POST 요청 보냄
        ResultActions resultActions = mockMvc.perform(post("/member/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // Then: 요청에 대한 기대 결과를 검증
        resultActions
                .andExpect(status().isCreated())  // HTTP 201 상태 확인
                .andExpect(jsonPath("$.code").value(201))  // code 값 확인
                .andExpect(jsonPath("$.message").value("정상적으로 생성되었습니다."))  // 메시지 확인
                .andExpect(jsonPath("$.data.id").value(1L));  // 생성된 회원 ID 확인

        // MemberService의 registerMember 메서드가 호출되었는지 검증
        verify(memberService).registerMember(any(MemberRegisterRequestDto.class));
    }

    @Test
    void registerMember_InvalidInput() throws Exception {
        // Given: 이메일 없이 요청을 만들어 유효성 검사 실패를 유도
        MemberRegisterRequestDto requestDto = new MemberRegisterRequestDto();
        requestDto.setName("테스트");
        requestDto.setPassword("password123!!");
        requestDto.setPasswordConfirm("password123!!");
        requestDto.setPhoneNumber("01012345678");
        requestDto.setNickname("testUser");

        // When: MockMvc를 사용하여 /member/register로 POST 요청을 보냄
        ResultActions resultActions = mockMvc.perform(post("/member/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // Then: 요청에 대한 기대 결과를 검증
        resultActions
                .andExpect(status().isBadRequest())  // HTTP 400 상태 확인
                .andExpect(jsonPath("$.code").value(400))  // code 값 확인
                .andExpect(jsonPath("$.message").value("이메일은 필수 항목입니다."));  // 오류 메시지 확인
    }

    @Test
    void registerMember_DuplicateEmail() throws Exception {
        // Given: 중복된 이메일로 요청을 만들어 이메일 중복 에러를 유도합니다.
        MemberRegisterRequestDto requestDto = new MemberRegisterRequestDto();
        requestDto.setName("테스트");
        requestDto.setEmail("existing@example.com");
        requestDto.setPassword("password123!!");
        requestDto.setPasswordConfirm("password123!!");
        requestDto.setPhoneNumber("01012345678");
        requestDto.setNickname("testUser");

        // 이메일 중복으로 MemberException을 발생시키도록 Mock 설정
        when(memberService.registerMember(any(MemberRegisterRequestDto.class)))
                .thenThrow(new MemberException(EXISTS_MEMBER_EMAIL));

        // When: MockMvc를 사용하여 /member/register로 POST 요청을 보냄
        ResultActions resultActions = mockMvc.perform(post("/member/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)));

        // Then: 요청에 대한 기대 결과를 검증
        resultActions
                .andExpect(status().isConflict())  // HTTP 409 상태 확인
                .andExpect(jsonPath("$.code").value(EXISTS_MEMBER_EMAIL.getErrorCode()))  // 에러 코드 확인
                .andExpect(jsonPath("$.message").value(EXISTS_MEMBER_EMAIL.getErrorMessage()));  // 에러 메시지 확인
    }
}