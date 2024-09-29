package com.ourhomerecipe.member.controller;

import static com.ourhomerecipe.domain.common.success.code.GlobalSuccessCode.*;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ourhomerecipe.domain.common.response.OhrResponse;
import com.ourhomerecipe.member.service.MemberFollowService;
import com.ourhomerecipe.security.service.MemberDetailsImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberFollowController {
	private final MemberFollowService memberFollowService;

	/**
	 * 회원 팔로우
	 * followingId 숫자만 받도록 정규식 설정
	 */
	@PostMapping("/follow/{followingId:[\\d]+}")
	public ResponseEntity<OhrResponse<?>> followMember(
		@AuthenticationPrincipal MemberDetailsImpl memberDetails,
		@PathVariable Long followingId
	) {
		memberFollowService.follow(memberDetails.getId(), followingId);
		return ResponseEntity.ok(new OhrResponse<>(SUCCESS));
	}

	/**
	 * 회원 언팔로우
	 * followingId 숫자만 받도록 정규식 설정
	 */
	@PostMapping("/unfollow/{followingId:[\\d]+}")
	public ResponseEntity<OhrResponse<?>> unfollowMember(
		@AuthenticationPrincipal MemberDetailsImpl memberDetails,
		@PathVariable Long followingId
	) {
		memberFollowService.unfollow(memberDetails.getId(), followingId);
		return ResponseEntity.ok(new OhrResponse<>(SUCCESS));
	}
}
