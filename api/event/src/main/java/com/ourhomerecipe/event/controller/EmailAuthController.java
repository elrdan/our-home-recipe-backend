package com.ourhomerecipe.event.controller;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ourhomerecipe.dto.email.request.EmailVerificationRequestDto;
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
		@RequestBody @Valid EmailVerificationRequestDto emailVerificationRequestDto) {
		emailService.sendEmail(emailVerificationRequestDto);
		return new ResponseEntity<>(OK);
	}
}
