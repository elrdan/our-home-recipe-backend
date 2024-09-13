package com.ourhomerecipe.infra.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // 스프링 설정 클래스임을 나타냄
public class SwaggerConfig {

    @Bean // 스프링 빈으로 등록하여 애플리케이션 컨텍스트에서 관리되도록 함
    public OpenAPI openAPI() {
        String jwt = "JWT"; // JWT 보안 스킴의 이름을 정의

        // 보안 요구사항을 생성하고 JWT 보안 스킴을 추가
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);

        // 보안 스킴을 정의하고 컴포넌트에 추가
        Components components = new Components().addSecuritySchemes(jwt, new SecurityScheme()
                .name(jwt) // 보안 스킴의 이름 설정
                .type(SecurityScheme.Type.HTTP) // 보안 스킴의 타입을 HTTP로 설정
                .scheme("bearer") // 인증 스킴을 Bearer로 설정
                .bearerFormat("JWT") // Bearer 토큰의 형식을 JWT로 설정
        );

        // OpenAPI 객체를 생성하고 설정을 추가
        return new OpenAPI()
                .components(new Components()) // 컴포넌트 초기화
                .info(apiInfo()) // API 정보 설정
                .addSecurityItem(securityRequirement) // 보안 요구사항 추가
                .components(components); // 정의한 보안 스킴 컴포넌트 추가
    }

    // API에 대한 기본 정보를 설정하는 메서드
    private Info apiInfo() {
        return new Info()
                .title("Member-API 테스트") // API의 제목 설정
                .description("member-api 테스트") // API에 대한 설명 추가
                .version("1.0.0"); // API의 버전 지정
    }
}
