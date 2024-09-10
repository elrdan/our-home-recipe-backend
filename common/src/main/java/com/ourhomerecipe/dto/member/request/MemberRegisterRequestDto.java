package com.ourhomerecipe.dto.member.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberRegisterRequestDto {
	@NotBlank(message = "이메일은 필수 항목입니다.")
	@Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message = "유효하지 않은 이메일 형식입니다.")
	private String email;

	@NotBlank(message = "비밀번호는 필수 항목입니다.")
	@Pattern(regexp = "^(?=.*[!@#$%^&*(),.?\":{}|<>])(?=.*[a-zA-Z])(?=.*\\d).{8,}$", message = "유효하지 않은 비밀번호 형식입니다.")
	private String password;

	@NotBlank(message = "비밀번호 확인은 필수 항목입니다.")
	@Pattern(regexp = "^(?=.*[!@#$%^&*(),.?\":{}|<>])(?=.*[a-zA-Z])(?=.*\\d).{8,}$", message = "유효하지 않은 비밀번호 형식입니다.")
	private String passwordConfirm;

	@NotBlank(message = "닉네임은 필수 항목입니다.")
	@Size(min = 2, max = 8, message = "닉네임은 2자 이상 8자 이하로 입력해주세요.")
	private String nickname;

	@NotBlank(message = "핸드폰 번호는 필수 항목입니다.")
	@Pattern(regexp = "^01([0|1|6|7|8|9])-?([0-9]{3,4})-?([0-9]{4})$", message = "핸드폰 번호를 올바르게 입력해주세요.")
	private String phoneNumber;

	@NotBlank(message = "이름은 필수 항목입니다.")
	@Pattern(regexp = "^[가-힣a-zA-Z]{2,20}$", message = "이름은 한글 또는 영문 2자 이상 20자 이하로 입력해주세요.")
	private String name;
}