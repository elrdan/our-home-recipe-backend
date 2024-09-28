package com.ourhomerecipe.dto.member.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberMyProfileResDto {
	private Long id;

	private String email;

	private String nickname;

	private String phoneNumber;

	private String name;

	private String profileImage;

	private String introduce;
}
