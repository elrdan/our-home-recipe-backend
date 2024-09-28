package com.ourhomerecipe.dto.member.request;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberUpdateProfileReqDto {
	@Size(min = 2, max = 8, message = "닉네임은 2자 이상 8자 이하로 입력해주세요.")
	private String nickname;

	@Size(max = 25, message = "회원 소개글은 25자 이하로 입력해주세요.")
	private String introduce;
}
