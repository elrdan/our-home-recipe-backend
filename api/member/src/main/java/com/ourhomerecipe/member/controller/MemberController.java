package com.ourhomerecipe.member.controller;

import static com.ourhomerecipe.domain.common.error.code.MemberErrorCode.*;
import static com.ourhomerecipe.domain.common.error.code.SecurityErrorCode.*;
import static com.ourhomerecipe.domain.common.success.code.GlobalSuccessCode.*;
import static org.springframework.util.StringUtils.*;

import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ourhomerecipe.domain.common.response.OhrResponse;
import com.ourhomerecipe.dto.email.request.EmailAuthConfirmRequestDto;
import com.ourhomerecipe.dto.email.request.EmailAuthRequestDto;
import com.ourhomerecipe.dto.member.request.MemberLoginReqDto;
import com.ourhomerecipe.dto.member.request.MemberRegisterReqDto;
import com.ourhomerecipe.dto.member.request.MemberUpdateProfileReqDto;
import com.ourhomerecipe.member.exception.MemberException;
import com.ourhomerecipe.member.service.MemberService;
import com.ourhomerecipe.security.exception.CustomSecurityException;
import com.ourhomerecipe.security.jwt.JwtProvider;
import com.ourhomerecipe.security.service.MemberDetailsImpl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
	private final MemberService memberService;
	private final JwtProvider jwtProvider;
	private static final int PAGE_SIZE = 10;

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
	 * 내 프로필 조회
	 */
	@GetMapping("/me/profile")
	public ResponseEntity<OhrResponse<?>> getMeProfile(
		@AuthenticationPrincipal MemberDetailsImpl memberDetails
	) {
		return ResponseEntity.status(SUCCESS.getStatus())
			.body(new OhrResponse<>(memberService.getMeProfile(memberDetails)));
	}

	/**
	 * 내 프로필 수정
	 */
	@PostMapping("/me/profile")
	public ResponseEntity<OhrResponse<?>> updateMeProfile(
		@AuthenticationPrincipal MemberDetailsImpl memberDetails,
		@Valid @RequestPart(name = "member", required = false) MemberUpdateProfileReqDto updateProfileReqDto,
		@RequestParam(name = "profileImage", required = false) MultipartFile file
	) {
		if(updateProfileReqDto == null && (file == null || file.isEmpty())){
			throw new MemberException(NO_UPDATE_FIELDS);
		}

		return ResponseEntity.status(MEMBER_PROFILE_UPDATE_SUCCESS.getStatus())
			.body(new OhrResponse<>(MEMBER_PROFILE_UPDATE_SUCCESS,
				Map.of("id", memberService.updateMeProfile(memberDetails,
					updateProfileReqDto, file).getId())));
	}

	/**
	 * 회원 검색(닉네임)
	 */
	@GetMapping("/search/{nickname}")
	public ResponseEntity<OhrResponse<?>> getSearchMember(
		@AuthenticationPrincipal MemberDetailsImpl memberDetails,
		@PathVariable String nickname,
		@RequestParam("page") int page
	) {
		Pageable pageable = PageRequest.of(page, PAGE_SIZE);

		return ResponseEntity.status(SUCCESS.getStatus())
			.body(new OhrResponse<>(memberService.getSearchMember(pageable, memberDetails, nickname)));
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

	/**
	 * refreshToken를 사용해서 accessToken, refreshToken 재발급
	 */
	@GetMapping("/token/refresh")
	public ResponseEntity<OhrResponse<?>> updateAccessTokenAndRefreshToken(
		@AuthenticationPrincipal MemberDetailsImpl memberDetails,
		HttpServletRequest request
	) {
		String refreshToken = jwtProvider.extractToken(request);

		// 토큰이 없는 경우
		if (!hasText(refreshToken)) {
			throw new CustomSecurityException(VALIDATION_NOT_EXISTS_TOKEN_FAILED);
		}

		// 토큰이 검증이 실패한 경우
		if (!jwtProvider.validate(refreshToken)) {
			throw new CustomSecurityException(VALIDATION_TOKEN_FAILED);
		}

		return ResponseEntity.status(SUCCESS.getStatus())
			.body(new OhrResponse<>(memberService.getNewAccessTokenAndRefreshToken(memberDetails, refreshToken)));
	}
}
