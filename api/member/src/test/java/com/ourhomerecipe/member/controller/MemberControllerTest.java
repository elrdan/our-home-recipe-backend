package com.ourhomerecipe.member.controller;
import com.ourhomerecipe.domain.member.Member;
import com.ourhomerecipe.domain.member.repository.MemberRepository;
import com.ourhomerecipe.dto.member.request.MemberRegisterRequestDto;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers  // Testcontainers를 사용하여 독립된 MySQL 환경에서 테스트 실행
class MemberControllerTest {

    // MySQL Testcontainer 설정: 테스트마다 새로운 MySQL 환경을 제공
    @Container
    public static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")  // 데이터베이스 이름 설정
            .withUsername("test")         // 사용자 이름 설정
            .withPassword("test")         // 비밀번호 설정
            .withInitScript("db/initdb.d/1-schema.sql"); // 초기 스키마 설정

    // MySQL Testcontainer에서 제공한 설정 정보를 동적으로 Spring에 반영
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);  // 데이터베이스 URL 등록
        registry.add("spring.datasource.username", mysql::getUsername);  // 사용자 이름 등록
        registry.add("spring.datasource.password", mysql::getPassword);  // 비밀번호 등록
    }

    @LocalServerPort
    private int port;  // 테스트 시 사용할 랜덤 포트 설정

    @Autowired
    private MemberRepository memberRepository;  // 실제 데이터베이스 상호작용을 위한 MemberRepository

    // RestAssured가 사용할 포트를 설정
    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    @DisplayName("회원 가입 성공 테스트")  // 성공적인 회원 가입을 테스트
    void registerMember_Success() {
        // Given: 회원가입 요청에 필요한 데이터 준비
        MemberRegisterRequestDto requestDto = new MemberRegisterRequestDto();
        requestDto.setName("테스트");
        requestDto.setEmail("test@example.com");
        requestDto.setPassword("Pass123!@");
        requestDto.setPasswordConfirm("Pass123!@");
        requestDto.setPhoneNumber("010-1234-5678");
        requestDto.setNickname("testUser");

        // When: 회원가입 API 호출
        // Then: 회원이 성공적으로 생성되었는지 확인
        given()
                .contentType(ContentType.JSON)  // 요청 본문의 Content-Type 설정 (JSON 형식)
                .body(requestDto)  // 요청 본문으로 DTO 전송
                .when()
                .post("/member/register")  // POST 요청을 보냄 (회원가입 API)
                .then()
                .statusCode(201)  // 응답 상태 코드가 201(CREATED)인지 확인
                .body("code", equalTo(201))  // 응답 본문에서 상태 코드가 201인지 확인
                .body("message", equalTo("정상적으로 생성되었습니다."));  // 성공 메시지 확인
    }

    @Test
    @DisplayName("유효하지 않은 값 예외처리 테스트")  // 잘못된 입력값으로 인한 예외처리 테스트
    void registerMember_InvalidInput() {
        // Given: 유효하지 않은 회원가입 요청 데이터 준비 (이메일 누락)
        MemberRegisterRequestDto requestDto = new MemberRegisterRequestDto();
        requestDto.setName("테스트");
        requestDto.setPassword("pass23!!");
        requestDto.setPasswordConfirm("pass23!!");
        requestDto.setPhoneNumber("01012345678");
        requestDto.setNickname("testUser");

        // When: 잘못된 회원가입 API 호출
        // Then: 유효성 검사 실패로 400(BAD REQUEST) 응답 확인
        given()
                .contentType(ContentType.JSON)  // 요청 본문의 Content-Type 설정 (JSON 형식)
                .body(requestDto)  // 요청 본문으로 DTO 전송
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
        MemberRegisterRequestDto requestDto = new MemberRegisterRequestDto();
        requestDto.setName("테스트");
        requestDto.setEmail("existing@example.com");
        requestDto.setPassword("pass123!!");
        requestDto.setPasswordConfirm("pass123!!");
        requestDto.setPhoneNumber("01012345678");
        requestDto.setNickname("testUser");

        // When: 중복된 이메일로 회원가입 API 호출
        // Then: 중복된 이메일로 인해 409(CONFLICT) 응답 확인
        given()
                .contentType(ContentType.JSON)  // 요청 본문의 Content-Type 설정 (JSON 형식)
                .body(requestDto)  // 요청 본문으로 DTO 전송
                .when()
                .post("/member/register")  // POST 요청을 보냄 (회원가입 API)
                .then()
                .statusCode(409)  // 응답 상태 코드가 409(CONFLICT)인지 확인
                .body("errorCode", equalTo(409))  // 응답 본문에서 에러 코드가 409인지 확인
                .body("errorMessage", equalTo("이미 등록된 회원 이메일입니다."));  // 이메일 중복 에러 메시지 확인
    }
}
