package com.ourhomerecipe.member.controller;

import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.*;
import static io.restassured.RestAssured.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

import org.junit.jupiter.api.BeforeEach;
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
public class MemberFollowControllerTest extends BaseTest {
	private String accessToken;

	@BeforeEach
	public void setUpEachTest() {
		MemberLoginReqDto requestDto = MemberLoginReqDto.builder()
			.email("admin@gmail.com")
			.password("Admin1234!@")
			.build();

		// 로그인 API 호출 후 accessToken 추출
		accessToken = given(spec)
			.contentType(APPLICATION_JSON_VALUE)
			.body(requestDto)
			.when()
			.post("/member/login")
			.then()
			.statusCode(200)
			.extract()
			.path("data.accessToken");
	}

	@Test
	@DisplayName("1-1. 회원 팔로우")
	void followMemberSuccess() {
		Long followingId = 2L;

		// 회원 팔로우 API 문서화
		given(spec)
			.header("Authorization", "Bearer " + accessToken)
			.filter(document("회원 팔로우 API",
				resourceDetails()
					.tag("회원 API")
					.summary("회원 팔로우"),
				// 응답 필드 문서화
				responseFields(
					fieldWithPath("code").type(NUMBER).description("상태 코드"),
					fieldWithPath("message").type(STRING).description("상태 메시지")
				)))
			.contentType(APPLICATION_JSON_VALUE)  // 요청 본문의 Content-Type 설정 (JSON 형식)
		.when()
			.post("/member/follow/{followingId}", followingId)
		.then().log().all()  // 요청 및 응답 로그 출력
			.statusCode(200);
	}

	@Test
	@DisplayName("2-1. 회원 언팔로우")
	void unfollowMemberSuccess() {
		Long followingId = 2L;

		// 회원 팔로우 API 문서화
		given(spec)
			.header("Authorization", "Bearer " + accessToken)
			.filter(document("회원 언팔로우 API",
				resourceDetails()
					.tag("회원 API")
					.summary("회원 언팔로우"),
				// 응답 필드 문서화
				responseFields(
					fieldWithPath("code").type(NUMBER).description("상태 코드"),
					fieldWithPath("message").type(STRING).description("상태 메시지")
				)))
			.contentType(APPLICATION_JSON_VALUE)  // 요청 본문의 Content-Type 설정 (JSON 형식)
		.when()
			.post("/member/unfollow/{followingId}", followingId)
		.then().log().all()  // 요청 및 응답 로그 출력
			.statusCode(200);
	}
}
