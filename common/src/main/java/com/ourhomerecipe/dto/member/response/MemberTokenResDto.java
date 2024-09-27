package com.ourhomerecipe.dto.member.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor @AllArgsConstructor
public class MemberTokenResDto {
	private String accessToken;

	private String refreshToken;
}
