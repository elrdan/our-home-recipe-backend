package com.ourhomerecipe.infra.audit;

import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;

import org.springframework.data.auditing.DateTimeProvider;

public class AuditingDateTimeProvider implements DateTimeProvider {
	@Override
	public Optional<TemporalAccessor> getNow() {
		// 현재 시스템 시간을 반환합니다.
		return Optional.of(ZonedDateTime.now());
	}
}
