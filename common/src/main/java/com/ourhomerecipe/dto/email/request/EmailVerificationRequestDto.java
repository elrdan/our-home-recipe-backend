package com.ourhomerecipe.dto.email.request;

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
public class EmailVerificationRequestDto {
	@Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message = "유효하지 않은 이메일 형식입니다.")
	@NotBlank(message = "이메일은 필수 항목입니다.")
	private String email;

	@NotBlank(message = "인증 번호는 필수 항목입니다.")
	@Size(min = 6, max = 6, message = "인증 번호는 6자리 입니다.")
	private String authCode;
}