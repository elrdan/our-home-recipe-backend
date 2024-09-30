package com.ourhomerecipe.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class MutableBaseEntity extends BaseEntity {
	@LastModifiedBy
	private String updatedBy;				// 수정자

	@LastModifiedDate
	private LocalDateTime updatedAt;		// 수정일
}
