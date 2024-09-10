package com.ourhomerecipe.member.service;

import static com.ourhomerecipe.domain.common.error.code.MemberErrorCode.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ourhomerecipe.domain.member.Member;
import com.ourhomerecipe.domain.member.repository.MemberRepository;
import com.ourhomerecipe.dto.member.request.MemberRegisterRequestDto;
import com.ourhomerecipe.member.exception.MemberException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	/**
	 * 회원 등록
	 */
	@Transactional
	public Member registerMember(MemberRegisterRequestDto registerRequestDto) {
		existsByEmail(registerRequestDto.getEmail());														// 이메일 중복 확인
		validatePassword(registerRequestDto.getPassword(), registerRequestDto.getPasswordConfirm());		// 비밀번호 일치 확인
		existsByPhoneNumber(registerRequestDto.getPhoneNumber());											// 휴대폰 중복 확인
		existsByNickname(registerRequestDto.getNickname());													// 닉네임 중복 확인

		Member member = Member.fromMemberRegisterDto(registerRequestDto);
		member.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));

		Member createMember = memberRepository.save(member);

		return createMember;
	}

	/**
	 * email 중복 체크
	 */
	public void existsByEmail(String email) {
		if(memberRepository.existsByEmail(email)){
			throw new MemberException(EXISTS_MEMBER_EMAIL);
		}
	}

	/**
	 * password, passwordConfirm 일치 여부 체크
	 */
	private void validatePassword(String password, String passwordConfirm) {
		if (!password.equals(passwordConfirm)) {
			throw new MemberException(NOT_MATCHED_PASSWORD);
		}
	}

	/**
	 * phoneNumber 중복 체크
	 */
	public void existsByPhoneNumber(String phoneNumber) {
		if(memberRepository.existsByPhoneNumber(phoneNumber)){
			throw new MemberException(EXISTS_MEMBER_PHONE_NUMBER);
		}
	}

	/**
	 * nickname 중복 체크
	 */
	public void existsByNickname(String nickName) {
		if(memberRepository.existsByNickname(nickName)) {
			throw new MemberException(EXISTS_MEMBER_NICKNAME);
		}
	}
}
