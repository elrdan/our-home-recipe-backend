package com.ourhomerecipe.event.controller;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ourhomerecipe.dto.email.EmailVerificationDto;
import com.ourhomerecipe.event.service.EmailService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailAuthController {
	private final EmailService emailService;

	@PostMapping("/verification/send")
	public ResponseEntity sendMessage(
		@RequestBody @Valid EmailVerificationDto emailVerificationDto) {
		emailService.sendEmail(emailVerificationDto);
		return new ResponseEntity<>(OK);
	}
}
