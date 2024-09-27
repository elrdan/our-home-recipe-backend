package com.ourhomerecipe.member.controller;

import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.*;
import static com.ourhomerecipe.domain.common.error.code.MemberErrorCode.*;
import static io.restassured.RestAssured.*;
import static io.restassured.http.ContentType.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.restdocs.RestDocumentationExtension;

import com.ourhomerecipe.dto.email.request.EmailAuthConfirmRequestDto;
import com.ourhomerecipe.dto.email.request.EmailAuthRequestDto;
import com.ourhomerecipe.dto.member.request.MemberRegisterReqDto;
import com.ourhomerecipe.infra.config.BaseTest;
import com.ourhomerecipe.member.exception.MemberException;
import com.ourhomerecipe.member.service.MemberService;

@ExtendWith(RestDocumentationExtension.class)
@TestMethodOrder(value = MethodOrderer.DisplayName.class)
class MemberRegisterControllerTest extends BaseTest {
	@SpyBean
	private MemberService memberService;

	@BeforeEach
	public void setUpEachTest() {
		// 테스트마다 다른 설정 적용 가능
		doReturn(true).when(memberService).emailAuthCheck("test@example.com");
	}

	@Test
	@DisplayName("1-1. 회원가입 테스트 - 성공")
	void registerMemberSuccess() {
		MemberRegisterReqDto requestDto = MemberRegisterReqDto.builder()
			.name("테스트")
			.email("test@example.com")
			.password("Pass123!@")
			.passwordConfirm("Pass123!@")
			.phoneNumber("010-1234-5678")
			.nickname("testUser")
			.introduce("")
			.build();

		// 회원가입 API 문서화
		given(spec)
			.filter(document("회원 가입 API - 성공",
				resourceDetails()
					.tag("회원 API")
					.summary("회원 가입"),
				// 요청 필드 문서화
				requestFields(
					fieldWithPath("email").type(STRING).description("이메일(아이디)"),
					fieldWithPath("password").type(STRING).description("패스워드"),
					fieldWithPath("passwordConfirm").type(STRING).description("패스워드 확인"),
					fieldWithPath("phoneNumber").type(STRING).description("핸드폰 번호"),
					fieldWithPath("nickname").type(STRING).description("닉네임"),
					fieldWithPath("name").type(STRING).description("이름"),
					fieldWithPath("introduce").type(STRING).description("소개")
				),
				// 응답 필드 문서화
				responseFields(
					fieldWithPath("code").type(NUMBER).description("상태 코드"),
					fieldWithPath("message").type(STRING).description("상태 메시지"),
					fieldWithPath("data.id").type(NUMBER).description("아이디 고유번호")
				)))
			.contentType(JSON)  // 요청 본문의 Content-Type 설정 (JSON 형식)
			.body(requestDto)				// 요청 본문 데이터 설정
		.when()
			.post("/member/register")
		.then().log().all()  // 요청 및 응답 로그 출력
			.statusCode(201);
	}

	@Test
	@DisplayName("1-2. 회원가입 테스트 - 실패(유효하지 않은 값)")
	void registerMemberInvalidInput() {
		// Given: 유효하지 않은 회원가입 요청 데이터 준비 (필수 값 누락, 유효하지 않은 값)
		MemberRegisterReqDto requestDto = MemberRegisterReqDto.builder()
			.email("")
			.name("테스트124123124")
			.password("Pass123")
			.passwordConfirm("Pass123")
			.phoneNumber("012-1234-5678")
			.nickname("testUsertestUsertestUser")
			.introduce("")
			.build();

		// When: 잘못된 회원가입 API 호출
		// Then: 유효성 검사 실패로 400(BAD REQUEST) 응답 확인
		given(spec)
			// 문서화 필터 추가
			.filter(document("회원 가입 API - 실패(유효하지 않은 값)",
				resourceDetails()
					.tag("회원 API")
					.summary("회원 가입"),
				requestFields(
					fieldWithPath("email").type(STRING).description("이메일(아이디)"),
					fieldWithPath("password").type(STRING).description("패스워드"),
					fieldWithPath("passwordConfirm").type(STRING).description("패스워드 확인"),
					fieldWithPath("phoneNumber").type(STRING).description("핸드폰 번호"),
					fieldWithPath("nickname").type(STRING).description("닉네임"),
					fieldWithPath("name").type(STRING).description("이름"),
					fieldWithPath("introduce").type(STRING).description("소개")
				),
				responseFields(
					fieldWithPath("errorCode").description("에러 코드"),
					fieldWithPath("errorMessage").description("에러 메시지"),
					subsectionWithPath("validation").type(OBJECT).description("유효하지 않은 필드")
				)))
			.contentType(JSON)
			.body(requestDto)
		.when()
			.post("/member/register")
		.then().log().all()
			.statusCode(400);
	}

	@Test
	@DisplayName("1-3. 회원가입 테스트 - 실패(중복 이메일)")
	void registerMemberDuplicateEmail() {
		// Given: 회원가입 요청 데이터 준비(1-1 테스트에서 이미 가입된 정보)
		MemberRegisterReqDto requestDto = MemberRegisterReqDto.builder()
			.name("테스트")
			.email("test@example.com")
			.password("Pass123!@")
			.passwordConfirm("Pass123!@")
			.phoneNumber("010-1234-5678")
			.nickname("testUser")
			.introduce("")
			.build();

		// When: 중복된 이메일로 회원가입 API 호출
		// Then: 중복된 이메일로 인해 409(CONFLICT) 응답 확인
		given(spec)
			// 문서화 필터 추가
			.filter(document("회원 가입 API - 실패(중복 이메일)",
				resourceDetails()
					.tag("회원 API")
					.summary("회원 가입"),
				requestFields(
					fieldWithPath("email").type(STRING).description("이메일(아이디)"),
					fieldWithPath("password").type(STRING).description("패스워드"),
					fieldWithPath("passwordConfirm").type(STRING).description("패스워드 확인"),
					fieldWithPath("phoneNumber").type(STRING).description("핸드폰 번호"),
					fieldWithPath("nickname").type(STRING).description("닉네임"),
					fieldWithPath("name").type(STRING).description("이름"),
					fieldWithPath("introduce").type(STRING).description("소개")
				),
				responseFields(
					fieldWithPath("errorCode").description("에러 코드"),
					fieldWithPath("errorMessage").description("에러 메시지")
				)))
			.contentType(JSON)  // 요청 본문의 Content-Type 설정 (JSON 형식)
			.body(requestDto)  // 요청 본문으로 DTO 전송
		.when()
			.post("/member/register")  // POST 요청을 보냄 (회원가입 API)
		.then()
			.statusCode(409);  // 응답 상태 코드가 409(CONFLICT)인지 확인
	}

	@Test
	@DisplayName("2-1. 이메일 인증 코드 요청 - 성공")
	void registerMemberEmailAuthSuccess() {
		EmailAuthRequestDto requestDto = EmailAuthRequestDto.builder()
			.email("seongo0521@gmail.com")
			.build();

		// 이메일 인증 코드 요청 무시
		doNothing().when(memberService).emailAuth(any(EmailAuthRequestDto.class));

		// When: 이메일 인증 코드 요청 API 호출
		// Then: 정상 처리 200 응답 확인
		given(spec)
			// 문서화 필터 추가
			.filter(document("이메일 인증 코드 요청 API - 성공",
				resourceDetails()
					.tag("회원 API")
					.summary("이메일 인증 코드 요청"),
				requestFields(
					fieldWithPath("email").type(STRING).description("이메일(아이디)")
				),
				responseFields(
					fieldWithPath("code").type(NUMBER).description("상태 코드"),
					fieldWithPath("message").type(STRING).description("상태 메시지")
				)))
			.contentType(JSON)
			.body(requestDto)
		.when()
			.post("/member/email/auth/request")
		.then()
			.statusCode(200);
	}

	@Test
	@DisplayName("2-2. 이메일 인증 코드 요청 - 실패(이메일 서버 다운)")
	void registerMemberEmailAuthEmailServerError() {
		EmailAuthRequestDto requestDto = EmailAuthRequestDto.builder()
			.email("seongo0521@gmail.com")
			.build();

		// When: 이메일 인증 코드 요청 API 호출
		// Then: 이메일 서버 문제로 인해 504 응답 확인
		given(spec)
			// 문서화 필터 추가
			.filter(document("이메일 인증 코드 요청 API - 실패(이메일 서버 다운)",
				resourceDetails()
					.tag("회원 API")
					.summary("이메일 인증 코드 요청"),
				requestFields(
					fieldWithPath("email").type(STRING).description("이메일(아이디)")
				),
				responseFields(
					fieldWithPath("errorCode").type(NUMBER).description("상태 코드"),
					fieldWithPath("errorMessage").type(STRING).description("상태 메시지")
				)))
			.contentType(JSON)
			.body(requestDto)
		.when()
			.post("/member/email/auth/request")
		.then()
			.statusCode(504);
	}

	@Test
	@DisplayName("2-2. 이메일 인증 코드 요청 - 실패(회원 등록된 이메일)")
	void registerMemberEmailAuthExistsMemberError() {
		EmailAuthRequestDto requestDto = EmailAuthRequestDto.builder()
			.email("test@example.com")
			.build();

		// When: 이메일 인증 코드 요청 API 호출
		// Then: 이미 회원 등록된 이메일 409 응답 확인
		given(spec)
			// 문서화 필터 추가
			.filter(document("이메일 인증 코드 요청 API - 실패(회원 등록된 이메일)",
				resourceDetails()
					.tag("회원 API")
					.summary("이메일 인증 코드 요청"),
				requestFields(
					fieldWithPath("email").type(STRING).description("이메일(아이디)")
				),
				responseFields(
					fieldWithPath("errorCode").type(NUMBER).description("상태 코드"),
					fieldWithPath("errorMessage").type(STRING).description("상태 메시지")
				)))
			.contentType(JSON)
			.body(requestDto)
			.when()
			.post("/member/email/auth/request")
			.then()
			.statusCode(409);
	}

	@Test
	@DisplayName("3-1. 이메일 인증 코드 확인 - 성공")
	void registerMemberEmailAuthConfirmSuccess() {
		EmailAuthConfirmRequestDto requestDto = EmailAuthConfirmRequestDto.builder()
			.email("seongo0521@gmail.com")
			.authCode("123456")
			.build();

		// 이메일 인증 코드 확인 무시
		doNothing().when(memberService).emailAuthConfirm(any(EmailAuthConfirmRequestDto.class));

		// When: 이메일 인증 코드 확인 API 호출
		// Then: 정상 처리 200 응답 확인
		given(spec)
			// 문서화 필터 추가
			.filter(document("이메일 인증 코드 확인 API - 성공",
				resourceDetails()
					.tag("회원 API")
					.summary("이메일 인증 코드 확인"),
				requestFields(
					fieldWithPath("email").type(STRING).description("이메일(아이디)"),
					fieldWithPath("authCode").type(STRING).description("인증 코드")
				),
				responseFields(
					fieldWithPath("code").type(NUMBER).description("상태 코드"),
					fieldWithPath("message").type(STRING).description("상태 메시지")
				)))
			.contentType(JSON)
			.body(requestDto)
		.when()
			.post("/member/email/auth/confirm")
		.then()
			.statusCode(200);
	}

	@Test
	@DisplayName("3-2. 이메일 인증 코드 확인 - 실패(인증 코드 x)")
	void registerMemberEmailAuthConfirmError1() {
		EmailAuthConfirmRequestDto requestDto = EmailAuthConfirmRequestDto.builder()
			.email("seongo0521@gmail.com")
			.authCode("123456")
			.build();

		doThrow(new MemberException(NOT_MATCHED_AUTH_CODE))
			.when(memberService)
			.emailAuthConfirm(any(EmailAuthConfirmRequestDto.class));

		// When: 이메일 인증 코드 확인 API 호출
		// Then: 이메일 인증 코드가 존재하지 않음 401 응답 확인
		given(spec)
			// 문서화 필터 추가
			.filter(document("이메일 인증 코드 확인 API - 실패(인증 코드 x)",
				resourceDetails()
					.tag("회원 API")
					.summary("이메일 인증 코드 확인"),
				requestFields(
					fieldWithPath("email").type(STRING).description("이메일(아이디)"),
					fieldWithPath("authCode").type(STRING).description("인증 코드")
				),
				responseFields(
					fieldWithPath("errorCode").type(NUMBER).description("상태 코드"),
					fieldWithPath("errorMessage").type(STRING).description("상태 메시지")
				)))
			.contentType(JSON)
			.body(requestDto)
		.when()
			.post("/member/email/auth/confirm")
		.then()
			.statusCode(401);
	}
}