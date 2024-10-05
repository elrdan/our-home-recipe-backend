package com.ourhomerecipe.member.controller;

import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.*;
import static io.restassured.RestAssured.*;
import static io.restassured.http.ContentType.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.restdocs.RestDocumentationExtension;

import com.ourhomerecipe.domain.common.repository.RedisRepository;
import com.ourhomerecipe.dto.email.request.EmailAuthConfirmRequestDto;
import com.ourhomerecipe.dto.email.request.EmailAuthRequestDto;
import com.ourhomerecipe.dto.member.request.MemberLoginReqDto;
import com.ourhomerecipe.dto.member.request.MemberRegisterReqDto;
import com.ourhomerecipe.dto.member.request.MemberUpdateProfileReqDto;
import com.ourhomerecipe.infra.config.BaseTest;
import com.ourhomerecipe.member.service.MemberService;

import io.restassured.response.Response;

@ExtendWith(RestDocumentationExtension.class)
@TestMethodOrder(value = MethodOrderer.DisplayName.class)
public class MemberControllerTest extends BaseTest {
	@SpyBean
	private MemberService memberService;

	@SpyBean
	private RedisRepository redisRepository;

	private String accessToken;
	private String refreshToken;

	@BeforeEach
	public void setUpEachTest() {
		MemberLoginReqDto requestDto = MemberLoginReqDto.builder()
			.email("admin@gmail.com")
			.password("Admin1234!@")
			.build();


		// 로그인 API 호출 후 accessToken 추출
		Response response = given(spec)
			.contentType(JSON)
			.body(requestDto)
		.when()
			.post("/member/login")
		.then()
			.statusCode(200)
			.extract()
			.response(); // 전체 응답 추출

		accessToken = response.path("data.accessToken");
		refreshToken = response.path("data.refreshToken");
	}

	@Test
	@DisplayName("1-1. 회원가입")
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

		doReturn(true).when(memberService).emailAuthCheck("test@example.com");

		// 회원가입 API 문서화
		given(spec)
			.filter(document("회원 가입 API",
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
			.body(requestDto)                // 요청 본문 데이터 설정
			.when()
			.post("/member/register")
			.then().log().all()  // 요청 및 응답 로그 출력
			.statusCode(201);
	}

	@Test
	@DisplayName("2-1. 이메일 인증 코드 요청")
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
			.filter(document("이메일 인증 코드 요청 API",
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
	@DisplayName("3-1. 이메일 인증 코드 확인")
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
			.filter(document("이메일 인증 코드 확인 API",
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
	@DisplayName("4-1. 로그인")
	void loginMemberSuccess() {
		MemberLoginReqDto requestDto = MemberLoginReqDto.builder()
			.email("admin@gmail.com")
			.password("Admin1234!@")
			.build();

		// 로그인 API 문서화
		given(spec)
			.filter(document("로그인 API",
				resourceDetails()
					.tag("회원 API")
					.summary("로그인"),
				// 요청 필드
				requestFields(
					fieldWithPath("email").type(STRING).description("이메일(아이디)"),
					fieldWithPath("password").type(STRING).description("패스워드")
				),
				// 응답 필드
				responseFields(
					fieldWithPath("code").type(NUMBER).description("상태 코드"),
					fieldWithPath("message").type(STRING).description("상태 메시지"),
					subsectionWithPath("data").type(OBJECT).description("토큰 데이터")
				)))
			.contentType(JSON)
			.body(requestDto)
			.when()
			.post("/member/login")
			.then()
			.statusCode(200);
	}

	@Test
	@DisplayName("5-1. 내 프로필 조회")
	void getMeProfileSuccess() {
		// 내 프로필 조회 API 문서화
		given(spec)
			.header("Authorization", "Bearer " + accessToken)        // accessToken 설정 추가
			.filter(document("내 프로필 조회 API",
				resourceDetails()
					.tag("회원 API")
					.summary("내 프로필 조회"),
				// 응답 필드
				responseFields(
					fieldWithPath("code").type(NUMBER).description("상태 코드"),
					fieldWithPath("message").type(STRING).description("상태 메시지"),
					subsectionWithPath("data").type(OBJECT).description("내 프로필 데이터")
				)))
			.contentType(JSON)
		.when()
			.get("/member/me/profile")
		.then()
			.statusCode(200);
	}

	@Test
	@DisplayName("6-1. 내 프로필 수정")
	void updateMeProfileSuccess() {
		MemberUpdateProfileReqDto updateProfileReqDto = MemberUpdateProfileReqDto.builder()
			.nickname("관리자2")
			.introduce("")
			.build();

		// 내 프로필 업데이트 API 문서화
		given(spec)
			.header("Authorization", "Bearer " + accessToken)   // accessToken 설정 추가
			.filter(document("내 프로필 수정 API",
				resourceDetails()
					.tag("회원 API")
					.summary("내 프로필 수정"),
				// 요청 필드 (Multipart 파트 설명)
				requestParts(
					partWithName("member").description("프로필 업데이트 요청 데이터 (JSON 형식)"),
					partWithName("profileImage").optional().description("업데이트할 프로필 이미지 (JPEG/PNG 형식, 최대 크기: 5MB)")
				),
				// "member" 파트의 JSON 필드 설명 추가
				requestPartFields("member",
					fieldWithPath("nickname").description("닉네임"),
					fieldWithPath("introduce").description("자기소개(선택 사항)")
				),
				// 응답 필드
				responseFields(
					fieldWithPath("code").type(NUMBER).description("상태 코드"),
					fieldWithPath("message").type(STRING).description("상태 메시지"),
					subsectionWithPath("data").type(OBJECT).description("업데이트된 프로필 데이터").optional(),
					fieldWithPath("data.id").type(NUMBER).description("업데이트된 회원의 ID")
				)))
			.contentType(MULTIPART_FORM_DATA_VALUE)
			.multiPart("member", updateProfileReqDto, APPLICATION_JSON_VALUE)  // 'member' 필드에 JSON 데이터 추가
			.when()
			.post("/member/me/profile")
			.then()
			.statusCode(200);
	}

	@Test
	@DisplayName("7-1. 회원 검색")
	void getSearchMemberSuccess() {
		String nickname = "관리자 닉네임";
		int page = 0;

		given(spec)
			.header("Authorization", "Bearer " + accessToken)
			.filter(document("회원 검색 API",
				resourceDetails()
					.tag("회원 API")
					.summary("회원 검색"),
				// 응답 필드
				responseFields(
					fieldWithPath("code").type(NUMBER).description("상태 코드"),
					fieldWithPath("message").type(STRING).description("상태 메시지"),
					subsectionWithPath("data").type(OBJECT).description("회원 검색 데이터")
				)))
			.contentType(APPLICATION_JSON_VALUE)
		.when()
			.get("/member/search/{nickname}?page={page}", nickname, page)
		.then()
			.statusCode(200);
	}

	@Test
	@DisplayName("8-1. 토큰 재발급")
	void updateAccessTokenAndRefreshTokenSuccess() {
		Map<String, String> map = new HashMap<>();
		map.put("refreshToken", refreshToken);

		doReturn(map).when(redisRepository).getRefreshToken("admin@gmail.com");

		// 토큰 재발급 API 문서화
		given(spec)
			.header("Authorization", "Bearer " + refreshToken)        // accessToken 설정 추가
			.filter(document("토큰 재발급 API",
				resourceDetails()
					.tag("회원 API")
					.summary("토큰 재발급"),
				// 응답 필드
				responseFields(
					fieldWithPath("code").type(NUMBER).description("상태 코드"),
					fieldWithPath("message").type(STRING).description("상태 메시지"),
					subsectionWithPath("data").type(OBJECT).description("토큰 데이터")
				)))
			.contentType(JSON)
		.when()
			.get("/member/token/refresh")
		.then()
			.statusCode(200);
	}

	@Test
	@DisplayName("9-1. 닉네임 중복 확인")
	void checkNicknameMemberSuccess() {
		given(spec)
			.filter(document("닉네임 중복 확인 API",
				resourceDetails()
					.tag("회원 API")
					.summary("닉네임 중복 확인"),
				// 응답 필드
				responseFields(
					fieldWithPath("code").type(NUMBER).description("상태 코드"),
					fieldWithPath("message").type(STRING).description("상태 메시지")
				)))
			.contentType(JSON)
		.when()
			.param("nickname", "testUser1")
			.get("/member/check-nickname")
		.then()
			.statusCode(200);
	}

	@Test
	@DisplayName("10-1. 로그아웃")
	void logoutMemberSuccess() {
		// 로그아웃 API 문서화
		given(spec)
			.header("Authorization", "Bearer " + accessToken)        // accessToken 설정 추가
			.filter(document("로그아웃 API",
				resourceDetails()
					.tag("회원 API")
					.summary("로그아웃"),
				// 응답 필드
				responseFields(
					fieldWithPath("code").type(NUMBER).description("상태 코드"),
					fieldWithPath("message").type(STRING).description("상태 메시지")
				)))
			.contentType(JSON)
			.when()
			.post("/member/logout")
			.then()
			.statusCode(200);
	}
}
