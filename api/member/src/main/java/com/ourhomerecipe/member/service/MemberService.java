package com.ourhomerecipe.member.service;

import static com.ourhomerecipe.domain.common.error.code.EventErrorCode.*;
import static com.ourhomerecipe.domain.common.error.code.MemberErrorCode.*;
import static com.ourhomerecipe.domain.common.error.code.SecurityErrorCode.*;
import static org.springframework.util.StringUtils.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.ourhomerecipe.domain.common.repository.RedisRepository;
import com.ourhomerecipe.domain.member.Member;
import com.ourhomerecipe.domain.member.repository.MemberRepository;
import com.ourhomerecipe.dto.email.request.EmailAuthConfirmRequestDto;
import com.ourhomerecipe.dto.email.request.EmailAuthRequestDto;
import com.ourhomerecipe.dto.member.request.MemberLoginReqDto;
import com.ourhomerecipe.dto.member.request.MemberRegisterReqDto;
import com.ourhomerecipe.dto.member.request.MemberUpdateProfileReqDto;
import com.ourhomerecipe.dto.member.response.MemberMyProfileResDto;
import com.ourhomerecipe.dto.member.response.MemberSearchResDto;
import com.ourhomerecipe.dto.member.response.MemberTokenResDto;
import com.ourhomerecipe.member.exception.MemberException;
import com.ourhomerecipe.security.jwt.JwtProvider;
import com.ourhomerecipe.security.service.MemberDetailsImpl;

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
	private final JwtProvider jwtProvider;
	private final AuthenticationManager authenticationManager;

	@Value("${service-url}")
	private String serviceUrl;

	/**
	 * 회원 등록
	 */
	@Transactional
	public Member registerMember(MemberRegisterReqDto registerRequestDto) {
		existsByEmail(registerRequestDto.getEmail());														// 이메일 중복 확인
		emailAuthCheck(registerRequestDto.getEmail());														// 이메일 인증 확인
		validatePassword(registerRequestDto.getPassword(), registerRequestDto.getPasswordConfirm());		// 비밀번호 일치 확인
		existsByPhoneNumber(registerRequestDto.getPhoneNumber());											// 휴대폰 중복 확인

		// 닉네임 중복 확인
		if(checkNickname(registerRequestDto.getNickname())) {
			throw new MemberException(ALREADY_MEMBER_NICKNAME);
		}

		Member member = Member.fromMemberRegisterDto(registerRequestDto);
		member.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));

		return memberRepository.save(member);
	}

	/**
	 * 회원 로그인
	 */
	public MemberTokenResDto login(MemberLoginReqDto loginDto) {
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
			loginDto.getEmail(),
			loginDto.getPassword()
		);

		Authentication authentication = authenticationManager.authenticate(authenticationToken);
		MemberDetailsImpl memberDetails = (MemberDetailsImpl) authentication.getPrincipal();

		MemberTokenResDto memberTokenResDto = createToken(memberDetails);
		registerRedisRefreshToken(memberDetails, memberTokenResDto.getRefreshToken());

		return memberTokenResDto;
	}

	/**
	 * 회원 로그아웃
	 * jwt accessToken redis에 저장하여 관리
	 */
	public void logout(String accessToken) {
		long expiration = jwtProvider.getExpiration(accessToken);

		redisRepository.setBlackListToken(accessToken, expiration);
	}

	/**
	 * 회원 검색(닉네임)
	 */
	@Transactional(readOnly = true)
	public Page<MemberSearchResDto> getSearchMember(Pageable pageable, MemberDetailsImpl memberDetails, String nickname) {
		long currentMemberId = memberDetails.getId();
		return memberRepository.findByNicknameContaining(pageable, currentMemberId, nickname);

	}

	/**
	 * 내 프로필 조회
	 */
	@Transactional(readOnly = true)
	public MemberMyProfileResDto getMeProfile(MemberDetailsImpl memberDetails) {
		MemberMyProfileResDto meProfile = memberRepository.getMeProfile(memberDetails.getId());

		if(meProfile == null) {
			throw new MemberException(NOT_EXISTS_MEMBER);
		}

		return meProfile;
	}

	/**
	 * 내 프로필 수정
	 */
	@Transactional
	public Member updateMeProfile(
		MemberDetailsImpl memberDetails,
		MemberUpdateProfileReqDto updateProfileReqDto,
		MultipartFile file
	) {
		Member member = memberRepository.findById(memberDetails.getId())
			.orElseThrow(() -> new MemberException(NOT_EXISTS_MEMBER));

		String newNickname = updateProfileReqDto.getNickname();
		String newIntroduce = updateProfileReqDto.getIntroduce();
		boolean isChangeNickname = false;

		if(hasText(newNickname)) {
			if(newNickname.equals(member.getNickname()) || checkNickname(newNickname)) {
				throw new MemberException(ALREADY_MEMBER_NICKNAME);
			}

			isChangeNickname = true;
		}

		if (isChangeNickname) {
			member.updateProfile(newNickname, newIntroduce);
		} else {
			member.updateProfile(newIntroduce);
		}

		// 회원 프로필 이미지 변경
		if(file != null && file.isEmpty()) {
			// TODO - S3 설정 후 파일 업로드 처리 해야 함.
			String newProfileImage = "";
			member.updateProfileImage(newProfileImage);
		}

		return memberRepository.save(member);
	}

	/**
	 * 토큰 재발급
	 * 클라이언트에서 보낸 리프래시 토큰을 Redis에 저장되어있는 refreshToken과 비교를 하여 refreshToken이 유효한지 체크하고,
	 * 유효한 경우 accessToken과 refreshToken을 재발급 한다.
	 */
	public MemberTokenResDto getNewAccessTokenAndRefreshToken(MemberDetailsImpl memberDetails, String refreshToken) {
		Map redisRefreshToken = redisRepository.getRefreshToken(memberDetails.getUsername());

		if(refreshToken.equals(String.valueOf(redisRefreshToken.get("refreshToken")))) {
			MemberTokenResDto memberTokenResDto = createToken(memberDetails);
			registerRedisRefreshToken(memberDetails, memberTokenResDto.getRefreshToken());
			return memberTokenResDto;
		}else {
			throw new MemberException(VALIDATION_TOKEN_FAILED);
		}
	}

	/**
	 * email 중복 체크
	 */
	public void existsByEmail(String email) {
		if(memberRepository.existsByEmail(email)){
			throw new MemberException(ALREADY_MEMBER_EMAIL);
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
			throw new MemberException(ALREADY_MEMBER_PHONE_NUMBER);
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
			throw new MemberException(ALREADY_MEMBER_EMAIL);
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

	/**
	 * refreshToken redis 저장
	 */
	private void registerRedisRefreshToken(MemberDetailsImpl memberDetails, String refreshToken) {
		long refreshExpirationSeconds = jwtProvider.getRefreshExpirationMilliseconds();
		redisRepository.setRedisRefreshToken(memberDetails.getUsername(), refreshToken, refreshExpirationSeconds);
	}

	/**
	 * 닉네임 중복 체크
	 */
	public boolean checkNickname(String nickname) {
		return memberRepository.existsByNickname(nickname);
	}

	/**
	 * 토큰 생성(Access Token, Refresh Token)
	 */
	private MemberTokenResDto createToken(MemberDetailsImpl memberDetails) {
		String accessToken = jwtProvider.createAccessToken(memberDetails);
		String refreshToken = jwtProvider.createRefreshToken(memberDetails);

		return MemberTokenResDto.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}
}
