package com.ourhomerecipe.domain.member.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ourhomerecipe.dto.member.response.MemberMyProfileResDto;
import com.ourhomerecipe.dto.member.response.MemberSearchResDto;

public interface MemberCustom {
	/**
	 * 내 프로필 조회
	 */
	MemberMyProfileResDto getMeProfile(long id);

	/**
	 * 회원 검색(닉네임)
	 * 대소문자 구분 안함
	 */
	Page<MemberSearchResDto> findByNicknameContaining(Pageable pageable, long currentMemberId, String nickname);
}
