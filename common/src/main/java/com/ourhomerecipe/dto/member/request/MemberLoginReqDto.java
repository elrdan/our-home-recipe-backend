package com.ourhomerecipe.dto.member.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberLoginReqDto {
	@NotBlank(message = "비밀번호는 필수 항목입니다.")
	@Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message = "유효하지 않은 이메일 형식입니다.")
	private String email;

	@NotBlank(message = "비밀번호는 필수 항목입니다.")
	@Pattern(regexp = "^(?=.*[!@#$%^&*(),.?\":{}|<>])(?=.*[a-zA-Z])(?=.*\\d).{8,}$", message = "유효하지 않은 비밀번호 형식입니다.")
	private String password;
}
