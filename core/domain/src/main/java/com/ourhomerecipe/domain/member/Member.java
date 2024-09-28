package com.ourhomerecipe.domain.member;

import static com.ourhomerecipe.domain.member.enums.ProviderType.*;
import static com.ourhomerecipe.domain.member.enums.RoleType.*;
import static com.ourhomerecipe.domain.member.enums.StatusType.*;

import com.ourhomerecipe.domain.TimestampedEntity;
import com.ourhomerecipe.domain.member.enums.ProviderType;
import com.ourhomerecipe.domain.member.enums.RoleType;
import com.ourhomerecipe.domain.member.enums.StatusType;
import com.ourhomerecipe.dto.member.request.MemberRegisterReqDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class Member extends TimestampedEntity {
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

	// 회원 프로필 이미지
	private String profileImage;

	// 회원 소개
	private String introduce;

	// 회원 상태
	@Enumerated(EnumType.STRING)
	@Builder.Default
	private StatusType status = ACTIVE;

	// 회원 권한
	@Enumerated(EnumType.STRING)
	@Builder.Default
	private RoleType role = ROLE_USER;

	// 회원 제공자
	@Enumerated(EnumType.STRING)
	@Builder.Default
	private ProviderType provider = NONE;

	public Member(String email, String password, String nickname, String phoneNumber, String name, String introduce) {
		this.email = email;
		this.password = password;
		this.nickname = nickname;
		this.phoneNumber = phoneNumber;
		this.name = name;

		if(introduce != null) {
			this.introduce = introduce;
		}
	}

	public static Member fromMemberRegisterDto(MemberRegisterReqDto registerDto) {
		// TODO - 프로필 이미지 설정 추가해야 함.

		Member member = Member.builder()
			.email(registerDto.getEmail())
			.nickname(registerDto.getNickname())
			.phoneNumber(registerDto.getPhoneNumber())
			.name(registerDto.getName())
			.build();

		return member;
	}

	/**
	 * 닉네임의 경우 공백값이 올 수 없고, 소개의 경우는 공백값을 사용할 수 있게 처리
	 */
	public void updateProfile(String newNickname, String newIntroduce) {
		this.nickname = newNickname;
		this.introduce = newIntroduce;
	}

	public void updateProfile(String newIntroduce) {
		this.introduce = newIntroduce;
	}

	public void updateProfileImage(String newProfileImage) {

	}
}
