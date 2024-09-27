package com.ourhomerecipe.security.service;

import static com.ourhomerecipe.domain.common.error.code.MemberErrorCode.*;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ourhomerecipe.domain.member.Member;
import com.ourhomerecipe.domain.member.repository.MemberRepository;
import com.ourhomerecipe.security.exception.CustomSecurityException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberDetailsService implements UserDetailsService {
	private final MemberRepository memberRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new CustomSecurityException(CHECK_ID_OR_PASSWORD));

		return MemberDetailsImpl.from(member);
	}
}
