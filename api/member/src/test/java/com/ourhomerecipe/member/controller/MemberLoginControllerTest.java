package com.ourhomerecipe.member.controller;

import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.*;
import static io.restassured.RestAssured.*;
import static io.restassured.http.ContentType.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.restdocs.RestDocumentationExtension;

import com.ourhomerecipe.dto.member.request.MemberLoginReqDto;
import com.ourhomerecipe.infra.config.BaseTest;

@ExtendWith(RestDocumentationExtension.class)
@TestMethodOrder(value = MethodOrderer.DisplayName.class)
public class MemberLoginControllerTest extends BaseTest {
	@Test
	@DisplayName("1-1. 회원 로그인 테스트 - 성공")
	void loginMemberSuccess() {
		MemberLoginReqDto requestDto = MemberLoginReqDto.builder()
			.email("admin@gmail.com")
			.password("Admin1234!@")
			.build();

		// 로그인 API 문서화
		given(spec)
			.filter(document("회원 로그인 API - 성공",
				resourceDetails()
					.tag("회원 API")
					.summary("회원 로그인"),
				// 요청 필드
				requestFields(
					fieldWithPath("email").type(STRING).description("이메일(아이디)"),
					fieldWithPath("password").type(STRING).description("패스워드")
				),
				// 응답 필드
				responseFields(
					fieldWithPath("code").type(NUMBER).description("상태 코드"),
					fieldWithPath("message").type(STRING).description("상태 메시지"),
					subsectionWithPath("data").type(OBJECT).description("토큰값")
				)))
			.contentType(JSON)
			.body(requestDto)
		.when()
			.post("/member/login")
		.then()
			.statusCode(200);
	}
}
