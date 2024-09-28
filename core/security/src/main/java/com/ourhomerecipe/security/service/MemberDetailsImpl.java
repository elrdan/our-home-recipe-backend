package com.ourhomerecipe.security.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ourhomerecipe.domain.member.Member;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
public class MemberDetailsImpl implements UserDetails {
	private long id;
	private String email;
	@JsonIgnore
	private String password;
	private String nickname;
	private List<GrantedAuthority> authorities;

	public MemberDetailsImpl(long id, String email, String password, String nickname, List<GrantedAuthority> authorities) {
		this.id = id;
		this.email = email;
		this.password = password;
		this.nickname = nickname;
		this.authorities = authorities;
	}

	public static MemberDetailsImpl from(Member member) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(member.getRole().name()));

		return new MemberDetailsImpl(
			member.getId(),
			member.getEmail(),
			member.getPassword(),
			member.getNickname(),
			authorities
		);
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}