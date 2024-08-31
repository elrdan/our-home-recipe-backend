package com.ourhomerecipe.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseEntity {
	@CreatedBy
	@Column(updatable = false)
	private String createdBy;				// 생성자

	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createdAt;		// 생성일
}
