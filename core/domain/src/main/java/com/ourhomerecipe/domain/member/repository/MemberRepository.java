package com.ourhomerecipe.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ourhomerecipe.domain.member.Member;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberCustom {
	boolean existsByEmail(String email);							// 이메일 중복 확인

	boolean existsByPhoneNumber(String phoneNumber);				// 핸드폰 번호 중복 확인

	boolean existsByNickname(String nickName);						// 닉네임 중복 확인
}
