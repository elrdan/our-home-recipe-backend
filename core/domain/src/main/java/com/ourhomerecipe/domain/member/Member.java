package com.ourhomerecipe.domain.member;

import static com.ourhomerecipe.domain.member.enums.RoleType.*;
import static com.ourhomerecipe.domain.member.enums.StatusType.*;

import java.sql.Date;

import com.ourhomerecipe.domain.MutableBaseEntity;
import com.ourhomerecipe.domain.member.enums.ProviderType;
import com.ourhomerecipe.domain.member.enums.RoleType;
import com.ourhomerecipe.domain.member.enums.StatusType;
import com.ourhomerecipe.dto.member.request.MemberRegisterRequestDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends MutableBaseEntity {
	// 회원 식별자
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;

	// 회원 이메일
	private String email;

	// 회원 비밀번호
	@Setter
	private String password;

	// 회원 닉네임
	private String nickname;

	// 회원 전화번호
	private String phoneNumber;

	// 회원 이름
	private String name;

	// 회원 상태
	private StatusType status;

	// 회원 권한
	private RoleType role;

	// 회원 제공자
	private ProviderType provider;

	public Member(String email, String password, String nickname, String phoneNumber, String name) {
		this.email = email;
		this.password = password;
		this.nickname = nickname;
		this.phoneNumber = phoneNumber;
		this.name = name;
	}

	public static Member fromMemberRegisterDto(MemberRegisterRequestDto registerRequestDto) {
		Member member = Member.builder()
			.email(registerRequestDto.getEmail())
			.nickname(registerRequestDto.getNickname())
			.phoneNumber(registerRequestDto.getPhoneNumber())
			.name(registerRequestDto.getName())
			.status(ACTIVE)
			.role(ROLE_USER)
			.build();

		return member;
	}
}
