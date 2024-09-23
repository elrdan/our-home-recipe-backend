package com.ourhomerecipe.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.ourhomerecipe.infra.audit.AuditingDateTimeProvider;
import com.ourhomerecipe.infra.audit.SpringSecurityAuditorAware;

@Configuration
@EnableJpaAuditing(
	auditorAwareRef = "springSecurityAuditorAware",				// 어떤 사용자가 로그인했는지 파악할 수 있게 해주는 빈의 이름(e.g., 생성자 혹은 수정자) -> getCurrentAuditor() 현재 사용자 정보 반환
	dateTimeProviderRef = "auditingDateTimeProvider"			// 생성 시간 또는 수정 시간을 제공하는 빈의 이름
)
public class JpaAuditConfig {
	@Bean
	public AuditorAware<String> springSecurityAuditorAware() {
		return new SpringSecurityAuditorAware();
	}

	@Bean
	public DateTimeProvider auditingDateTimeProvider() {
		return new AuditingDateTimeProvider();
	}
}
