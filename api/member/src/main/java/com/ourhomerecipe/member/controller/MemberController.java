package com.ourhomerecipe.member.controller;

import static com.ourhomerecipe.domain.common.success.code.GlobalSuccessCode.*;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ourhomerecipe.domain.common.response.OhrResponse;
import com.ourhomerecipe.dto.email.request.EmailAuthConfirmRequestDto;
import com.ourhomerecipe.dto.email.request.EmailAuthRequestDto;
import com.ourhomerecipe.dto.member.request.MemberRegisterRequestDto;
import com.ourhomerecipe.member.service.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
	private final MemberService memberService;

	/**
	 * 회원 등록
	 */
	@PostMapping("/register")
	public ResponseEntity<OhrResponse<?>> registerMember(@Valid @RequestBody MemberRegisterRequestDto registerRequestDto) {
		return ResponseEntity.status(CREATE.getStatus())
			.body(new OhrResponse<>(CREATE, Map.of("id", memberService.registerMember(registerRequestDto).getId())));
	}

	/**
	 * 이메일 인증 코드 요청
	 */
	@PostMapping("/email/auth/request")
	public ResponseEntity<OhrResponse<?>> emailAuthRequest(
		@RequestBody @Valid EmailAuthRequestDto emailAuthDto) {
		memberService.emailAuth(emailAuthDto);

		return ResponseEntity.status(EMAIL_SEND_SUCCESS.getStatus())
			.body(new OhrResponse<>(EMAIL_SEND_SUCCESS));
	}

	/**
	 * 이메일 인증 코드 확인
	 */
	@PostMapping("/email/auth/confirm")
	public ResponseEntity<OhrResponse<?>> emailAuthConfirm(
		@RequestBody @Valid EmailAuthConfirmRequestDto emailAuthConfirmDto) {
		memberService.emailAuthConfirm(emailAuthConfirmDto);

		return ResponseEntity.status(EMAIL_AUTH_CONFIRM.getStatus())
			.body(new OhrResponse<>(EMAIL_AUTH_CONFIRM));
	}

}
