package com.ourhomerecipe.member.controller;

import com.ourhomerecipe.domain.member.Member;
import com.ourhomerecipe.domain.member.repository.MemberRepository;
import com.ourhomerecipe.dto.member.request.MemberRegisterRequestDto;
import config.TestContainerConfig;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.document;
//import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
// RestDocs가 RestDocumentationContextProvider를 테스트 메소드에 주입할 수 있도록 활성화
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MemberControllerTest extends TestContainerConfig {

    @LocalServerPort
    private int port;  // 테스트 시 사용할 랜덤 포트 설정

    @Autowired
    private MemberRepository memberRepository;  // 실제 데이터베이스 상호작용을 위한 MemberRepository

    private RequestSpecification documentationSpec;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) { // RequestSpecification을 설정하는 ReauestSpecBuilder 생성
        // documentationSpec에 포트, baseUri, basePath 및 문서화 필터를 모두 설정
        this.documentationSpec = new RequestSpecBuilder()
                .setPort(port)  // 포트 설정 -> 랜덤포트를 생성
                .setBaseUri("http://localhost")  // 기본 URI 설정
                .setBasePath("/v1")  // 기본 경로 설정
                .addFilter(RestAssuredRestDocumentation.documentationConfiguration(restDocumentation))  // 문서화 필터 설정
                .build();
        // RestAssured의 포트와 경로를 추가로 설정할 필요 없음 (documentationSpec에 이미 포함됨)
        // 기본 RestAssured 설정도 포트와 경로를 명시적으로 설정 (필요시)
        RestAssured.port = port;
    }

    @Test
    @DisplayName("회원 가입 성공 테스트")
        // 성공적인 회원 가입을 테스트
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

        // When: 회원가입 API 호출
        // Then: 회원이 성공적으로 생성되었는지 확인
        RestAssured.given(documentationSpec)
                .contentType(ContentType.JSON)  // 요청 본문의 Content-Type 설정 (JSON 형식)
                .body(requestDto)  // 요청 본문으로 DTO 전송
                // given과 when사이에 들어가면 요청과 응답 모두 문서화 할 수 있다.
                .filter(document("registerMember_Success",
                        getRequestFieldsSnippet(),
                        getResponseFieldsSnippet()))
                .when()
                .post("/member/register")  // POST 요청을 보냄 (회원가입 API)
                .then()
                .log().all()  // 요청 및 응답 로그 출력
                .statusCode(201)  // 응답 상태 코드가 201(CREATED)인지 확인
                .body("code", equalTo(201))  // 응답 본문에서 상태 코드가 201인지 확인
                .body("message", equalTo("정상적으로 생성되었습니다."))  // 성공 메시지 확인
                .body("data.id", notNullValue());

    }


    @Test
    @DisplayName("유효하지 않은 값 예외처리 테스트")  // 잘못된 입력값으로 인한 예외처리 테스트
    void registerMember_InvalidInput() {
        // Given: 유효하지 않은 회원가입 요청 데이터 준비 (이메일 누락)
        MemberRegisterRequestDto requestDto = MemberRegisterRequestDto.builder()
                .name("테스트")
                .password("Pass123!@")
                .passwordConfirm("Pass123!@")
                .phoneNumber("010-1234-5678")
                .nickname("testUser")
                .build();

        // When: 잘못된 회원가입 API 호출
        // Then: 유효성 검사 실패로 400(BAD REQUEST) 응답 확인
        RestAssured.given(documentationSpec)
                .contentType(ContentType.JSON)  // 요청 본문의 Content-Type 설정 (JSON 형식)
                .body(requestDto)  // 요청 본문으로 DTO 전송
                // 문서화 필터 추가
                .filter(document("registerMember_InvalidInput",
                        getRequestFieldsSnippet(),
                        responseFields(
                                fieldWithPath("errorCode").description("에러 코드"),
                                fieldWithPath("errorMessage").description("에러 메시지"),
                                fieldWithPath("validation.email").description("이메일 유효성 검사 오류 메시지")
                        )
                ))
                .when()
                .post("/member/register")  // POST 요청을 보냄 (회원가입 API)
                .then()
                .statusCode(400)  // 응답 상태 코드가 400(BAD REQUEST)인지 확인
                .body("errorCode", equalTo(400))  // 응답 본문에서 에러 코드가 400인지 확인
                .body("errorMessage", equalTo("유효성 검사 오류"))  // 에러 메시지 확인
                .body("validation.email", equalTo("이메일은 필수 항목입니다."));  // 이메일 유효성 검사 메시지 확인
    }

    @Test
    @DisplayName("중복 이메일 회원가입 테스트")  // 중복된 이메일로 인한 회원가입 실패 테스트
    void registerMember_DuplicateEmail() {
        // Given: 이미 존재하는 이메일로 회원 저장
        Member existingMember = new Member();
        existingMember.setEmail("existing@example.com");
        memberRepository.save(existingMember);  // 기존 회원을 DB에 저장하여 중복된 상태를 만듦

        // 새로운 회원가입 요청 데이터 준비 (중복된 이메일 사용)
        MemberRegisterRequestDto requestDto = MemberRegisterRequestDto.builder()
                .name("테스트")
                .email("existing@example.com")
                .password("Pass123!@")
                .passwordConfirm("Pass123!@")
                .phoneNumber("010-1234-5678")
                .nickname("testUser")
                .build();

        // When: 중복된 이메일로 회원가입 API 호출
        // Then: 중복된 이메일로 인해 409(CONFLICT) 응답 확인
        RestAssured.given(documentationSpec)
                .contentType(ContentType.JSON)  // 요청 본문의 Content-Type 설정 (JSON 형식)
                .body(requestDto)  // 요청 본문으로 DTO 전송
                // 문서화 필터 추가
                .filter(document("registerMember_DuplicateEmail",
                        getRequestFieldsSnippet(),
                        responseFields(
                                fieldWithPath("errorCode").description("에러 코드"),
                                fieldWithPath("errorMessage").description("에러 메시지")
                        )
                ))
                .when()
                .post("/member/register")  // POST 요청을 보냄 (회원가입 API)
                .then()
                .statusCode(409)  // 응답 상태 코드가 409(CONFLICT)인지 확인
                .body("errorCode", equalTo(409))  // 응답 본문에서 에러 코드가 409인지 확인
                .body("errorMessage", equalTo("이미 등록된 회원 이메일입니다."));  // 이메일 중복 에러 메시지 확인
    }
}