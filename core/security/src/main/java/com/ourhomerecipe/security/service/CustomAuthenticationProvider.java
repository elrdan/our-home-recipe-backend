package com.ourhomerecipe.security.service;

import static com.ourhomerecipe.domain.common.error.code.MemberErrorCode.*;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.ourhomerecipe.security.exception.CustomSecurityException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {
	private final MemberDetailsService memberDetailsService;
	private final PasswordEncoder passwordEncoder;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String email = authentication.getName();
		String password = authentication.getCredentials().toString();

		MemberDetailsImpl memberDetails = (MemberDetailsImpl)memberDetailsService.loadUserByUsername(email);

		// 비밀번호 확인
		if(!passwordEncoder.matches(password, memberDetails.getPassword())) {
			throw new CustomSecurityException(CHECK_ID_OR_PASSWORD);
		}

		return new UsernamePasswordAuthenticationToken(memberDetails, password, memberDetails.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
}
