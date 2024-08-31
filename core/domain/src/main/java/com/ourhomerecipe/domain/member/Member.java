package com.ourhomerecipe.domain.member;

import com.ourhomerecipe.domain.MutableBaseEntity;
import com.ourhomerecipe.domain.enums.ProviderType;
import com.ourhomerecipe.domain.enums.RoleType;
import com.ourhomerecipe.domain.enums.StatusType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder @NoArgsConstructor @AllArgsConstructor
public class Member extends MutableBaseEntity {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;										// 회원 식별자

	private String email;									// 회원 이메일

	private String password;								// 회원 비밀번호

	private String nickname;								// 회원 닉네임

	private String phoneNumber;								// 회원 전화번호

	private String name;									// 회원 이름

	private StatusType status;								// 회원 상태

	private RoleType role;									// 회원 권한

	private ProviderType provider;							// 회원 제공자
}
