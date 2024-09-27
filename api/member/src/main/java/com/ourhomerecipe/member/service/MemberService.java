package com.ourhomerecipe.member.service;

import static com.ourhomerecipe.domain.common.error.code.EventErrorCode.*;
import static com.ourhomerecipe.domain.common.error.code.MemberErrorCode.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.ourhomerecipe.domain.common.repository.RedisRepository;
import com.ourhomerecipe.domain.member.Member;
import com.ourhomerecipe.domain.member.repository.MemberRepository;
import com.ourhomerecipe.dto.email.request.EmailAuthConfirmRequestDto;
import com.ourhomerecipe.dto.email.request.EmailAuthRequestDto;
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
	private final RedisRepository redisRepository;
	private final RestTemplate restTemplate;

	@Value("${service-url}")
	private String serviceUrl;

	/**
	 * 회원 등록
	 */
	@Transactional
	public Member registerMember(MemberRegisterRequestDto registerRequestDto) {
		existsByEmail(registerRequestDto.getEmail());														// 이메일 중복 확인
		emailAuthCheck(registerRequestDto.getEmail());														// 이메일 인증 확인
		validatePassword(registerRequestDto.getPassword(), registerRequestDto.getPasswordConfirm());		// 비밀번호 일치 확인
		existsByPhoneNumber(registerRequestDto.getPhoneNumber());											// 휴대폰 중복 확인
		existsByNickname(registerRequestDto.getNickname());													// 닉네임 중복 확인

		Member member = Member.fromMemberRegisterDto(registerRequestDto);
		member.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));

		return memberRepository.save(member);
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

	/**
	 * 이메일 인증 여부 체크
	 */
	public boolean emailAuthCheck(String email) {
		Map emailVerificationInfo = redisRepository.getEmailCodeAndConfirm(email);

		String confirm = (String)emailVerificationInfo.get("confirm");

		if(confirm == null || !confirm.equals("Y")) {
			throw new MemberException(NOT_AUTHENTICATED_EMAIL);
		}

		return true;
	}

	/**
	 * 이메일 인증 코드 요청
	 */
	public void emailAuth(EmailAuthRequestDto emailAuthDto) {
		String email = emailAuthDto.getEmail();

		if(memberRepository.existsByEmail(email)){
			throw new MemberException(EXISTS_MEMBER_EMAIL);
		}

		String authCode = generateCode();
		sendEmailVerification(email, authCode);
	}

	/**
	 * 이메일 인증 코드 생성
	 */
	private String generateCode() {
		Random random = new Random();
		int code = random.nextInt(900000) + 100000; // 100000 ~ 999999 범위의 숫자 생성
		return String.valueOf(code);
	}

	/**
	 * 이메일 서버에 이메일 인증 코드 요청
	 */
	public void sendEmailVerification(String email, String authCode) {
		String url = serviceUrl + "/v1/email/verification/send";

		try{
			Map<String, String> emailInfo = new HashMap<>();
			emailInfo.put("email", email);
			emailInfo.put("authCode", authCode);

			ResponseEntity<String> response = restTemplate.postForEntity(url, emailInfo, String.class);

			if (!response.getStatusCode().is2xxSuccessful()) {
				throw new MemberException(UNABLE_TO_SEND_EMAIL);
			}

			redisRepository.setEmailCodeAndConfirm(email, authCode, "N");
		}catch (RestClientException e) {
			throw new MemberException(UNABLE_TO_SEND_EMAIL);
		}
	}

	/**
	 * 이메일 인증 코드 확인 및 인증 상태 변경(Confirm 값 변경)
	 */
	public void emailAuthConfirm(EmailAuthConfirmRequestDto emailAuthDto) {

		Map redisEmailInfo = redisRepository.getEmailCodeAndConfirm(emailAuthDto.getEmail());

		Optional.ofNullable(redisEmailInfo.get("authCode"))
			.ifPresentOrElse(
				authCode -> {
					if(emailAuthDto.getAuthCode().equals(authCode)) {
						redisRepository.setEmailCodeAndConfirm(emailAuthDto.getEmail(), (String)authCode, "Y");
					}else {
						throw new MemberException(NOT_MATCHED_AUTH_CODE);
					}
				},
				() -> {
					throw new MemberException(NOT_MATCHED_AUTH_CODE);
				}
			);
	}
}
