package com.ourhomerecipe.member.controller;

import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.*;
import static io.restassured.RestAssured.*;
import static io.restassured.http.ContentType.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;

import com.ourhomerecipe.dto.member.request.MemberRegisterRequestDto;
import com.ourhomerecipe.infra.config.TestContainerConfig;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

@ExtendWith(RestDocumentationExtension.class)
// RestDocs가 RestDocumentationContextProvider를 테스트 메소드에 주입할 수 있도록 활성화
@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestMethodOrder(value = MethodOrderer.DisplayName.class)
class MemberControllerTest extends TestContainerConfig {

	@LocalServerPort
	private int port;  // 테스트 시 사용할 랜덤 포트 설정

	private RequestSpecification spec;

	@BeforeEach
	void setUp(RestDocumentationContextProvider restDocumentation) { // RequestSpecification을 설정하는 ReauestSpecBuilder 생성
		// documentationSpec에 포트, baseUri, basePath 및 문서화 필터를 모두 설정
		this.spec = new RequestSpecBuilder()
			.setPort(port)  // 포트 설정 -> 랜덤포트를 생성
			.setBaseUri("http://localhost")  // 기본 URI 설정
			.setBasePath("/v1")  // 기본 경로 설정
			.addFilter(documentationConfiguration(restDocumentation))  // 문서화 필터 설정
			.build();
		// RestAssured의 포트와 경로를 추가로 설정할 필요 없음 (documentationSpec에 이미 포함됨)
		// 기본 RestAssured 설정도 포트와 경로를 명시적으로 설정 (필요시)
		RestAssured.port = port;
	}

	@Test
	@DisplayName("1-1. 회원가입 테스트 - 성공")
	void registerMember_Success() {
		// Given: 회원가입 요청에 필요한 데이터 준비
		MemberRegisterRequestDto requestDto = MemberRegisterRequestDto.builder()
			.name("테스트")
			.email("test@example.com")
			.password("Pass123!@")
			.passwordConfirm("Pass123!@")
			.phoneNumber("010-1234-5678")
			.nickname("testUser")
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
					fieldWithPath("name").type(STRING).description("이름")
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
	@DisplayName("1-2. 회원가입 테스트 - 실패 (유효하지 않은 값)")
		// 유효하지 않는 입력값으로 인한 회원가입 실패
	void registerMember_InvalidInput() {
		// Given: 유효하지 않은 회원가입 요청 데이터 준비 (필수 값 누락, 유효하지 않은 값)
		MemberRegisterRequestDto requestDto = MemberRegisterRequestDto.builder()
			.name("테스트124123124")
			.password("Pass123")
			.passwordConfirm("Pass123")
			.phoneNumber("012-1234-5678")
			.nickname("testUsertestUsertestUser")
			.build();

		// When: 잘못된 회원가입 API 호출
		// Then: 유효성 검사 실패로 400(BAD REQUEST) 응답 확인
		given(spec)
			// 문서화 필터 추가
			.filter(document("회원 가입 API - 실패 (유효하지 않은 값)",
				resourceDetails()
					.tag("회원 API")
					.summary("회원 가입"),
				requestFields(
					fieldWithPath("email").type(NULL).description("이메일(아이디)"),
					fieldWithPath("password").type(STRING).description("패스워드"),
					fieldWithPath("passwordConfirm").type(STRING).description("패스워드 확인"),
					fieldWithPath("phoneNumber").type(STRING).description("핸드폰 번호"),
					fieldWithPath("nickname").type(STRING).description("닉네임"),
					fieldWithPath("name").type(STRING).description("이름")
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
	@DisplayName("1-3. 회원가입 테스트 - 실패 (중복 이메일)")
		// 중복된 이메일로 인한 회원가입 실패 테스트
	void registerMember_DuplicateEmail() {
		// Given: 회원가입 요청 데이터 준비(1-1 테스트에서 이미 가입된 정보)
		MemberRegisterRequestDto requestDto = MemberRegisterRequestDto.builder()
			.name("테스트")
			.email("test@example.com")
			.password("Pass123!@")
			.passwordConfirm("Pass123!@")
			.phoneNumber("010-1234-5678")
			.nickname("testUser")
			.build();

		// When: 중복된 이메일로 회원가입 API 호출
		// Then: 중복된 이메일로 인해 409(CONFLICT) 응답 확인
		given(spec)
			// 문서화 필터 추가
			.filter(document("회원 가입 API - 실패 (중복 이메일)",
				resourceDetails()
					.tag("회원 API")
					.summary("회원 가입"),
				requestFields(
					fieldWithPath("email").type(STRING).description("이메일(아이디)"),
					fieldWithPath("password").type(STRING).description("패스워드"),
					fieldWithPath("passwordConfirm").type(STRING).description("패스워드 확인"),
					fieldWithPath("phoneNumber").type(STRING).description("핸드폰 번호"),
					fieldWithPath("nickname").type(STRING).description("닉네임"),
					fieldWithPath("name").type(STRING).description("이름")
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
}