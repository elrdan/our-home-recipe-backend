package com.ourhomerecipe.member.controller;

import static com.ourhomerecipe.domain.common.error.code.SecurityErrorCode.*;
import static com.ourhomerecipe.domain.common.success.code.GlobalSuccessCode.*;
import static org.springframework.util.StringUtils.*;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ourhomerecipe.domain.common.response.OhrResponse;
import com.ourhomerecipe.dto.email.request.EmailAuthConfirmRequestDto;
import com.ourhomerecipe.dto.email.request.EmailAuthRequestDto;
import com.ourhomerecipe.dto.member.request.MemberLoginReqDto;
import com.ourhomerecipe.dto.member.request.MemberRegisterReqDto;
import com.ourhomerecipe.member.service.MemberService;
import com.ourhomerecipe.security.exception.CustomSecurityException;
import com.ourhomerecipe.security.jwt.JwtProvider;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
	private final MemberService memberService;
	private final JwtProvider jwtProvider;

	/**
	 * 회원 등록
	 */
	@PostMapping("/register")
	public ResponseEntity<OhrResponse<?>> registerMember(@Valid @RequestBody MemberRegisterReqDto registerRequestDto) {
		return ResponseEntity.status(CREATE.getStatus())
			.body(new OhrResponse<>(CREATE, Map.of("id", memberService.registerMember(registerRequestDto).getId())));
	}

	/**
	 * 회원 로그인
	 */
	@PostMapping("/login")
	public ResponseEntity<OhrResponse<?>> loginMember(
		@Valid @RequestBody MemberLoginReqDto loginDto
	) {
		return ResponseEntity.status(SUCCESS.getStatus())
			.body(new OhrResponse<>(memberService.login(loginDto)));
	}

	/**
	 * 회원 로그아웃
	 */
	@PostMapping("/logout")
	public ResponseEntity<OhrResponse<?>> logoutMember(
		HttpServletRequest request
	) {
		String token = jwtProvider.extractToken(request);

		if(hasText(token) && jwtProvider.validate(token)) {
			memberService.logout(token);
		}else {
			throw new CustomSecurityException(VALIDATION_NOT_EXISTS_TOKEN_FAILED);
		}

		return ResponseEntity.status(LOGOUT.getStatus())
			.body(new OhrResponse<>(LOGOUT));
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
