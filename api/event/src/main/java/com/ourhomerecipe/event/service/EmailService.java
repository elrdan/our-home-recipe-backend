package com.ourhomerecipe.event.service;

import static com.ourhomerecipe.domain.common.error.code.EventErrorCode.*;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.ourhomerecipe.dto.email.request.EmailVerificationRequestDto;
import com.ourhomerecipe.event.exception.EventException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
	private final JavaMailSender mailSender;

	/**
	 * 이메일 전송
	 */
	public void sendEmail(EmailVerificationRequestDto emailVerificationRequestDto) {
		MimeMessage message = createEmailForm(emailVerificationRequestDto);

		mailSender.send(message);
	}

	/**
	 * 인증 이메일 html 메시지 생성
	 */
	private MimeMessage createEmailForm(EmailVerificationRequestDto emailVerificationRequestDto) {
		String email = emailVerificationRequestDto.getEmail();
		String title = "우리의 집 레시피 이메일 인증 번호";
		String authCode = emailVerificationRequestDto.getAuthCode();

		try{
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			helper.setTo(email);
			helper.setSubject(title);

			String htmlContent = "<h1>우리의 집 레시피</h1>"
				+ "<p>이메일 인증 코드: <strong>" + authCode + "</strong></p>";
			helper.setText(htmlContent, true);

			return message;
		}catch(MessagingException e) {
			log.debug("MailService.sendEmail exception occur toEmail: {}, " +
				"title: {}, authCode: {}", email, title, authCode);
			throw new EventException(UNABLE_TO_SEND_EMAIL);
		}
	}
}