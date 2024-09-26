package com.ourhomerecipe.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class TimestampedEntity {
	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createdAt;		// 생성일

	@LastModifiedDate
	private LocalDateTime updatedAt;		// 수정일
}