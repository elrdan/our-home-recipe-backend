package com.ourhomerecipe.recipe.controller;

import static com.ourhomerecipe.domain.common.success.code.GlobalSuccessCode.*;

import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ourhomerecipe.domain.common.response.OhrResponse;
import com.ourhomerecipe.dto.recipe.request.RecipeRegisterReqDto;
import com.ourhomerecipe.recipe.service.RecipeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recipe")
public class RecipeController {
	private final RecipeService recipeService;
	private static final int PAGE_SIZE = 9;

	/**
	 * 레시피 등록
	 */
	@PostMapping("/register")
	public ResponseEntity<OhrResponse<?>> registerRecipe(
		@Valid @RequestBody RecipeRegisterReqDto recipeRegisterReqDto
	) {
		return ResponseEntity.status(CREATE.getStatus())
			.body(new OhrResponse<>(
				CREATE,
				Map.of("id", recipeService.registerRecipe(recipeRegisterReqDto).getId())
			));
	}

	/**
	 * 레시피 메타 데이터 조회
	 */
	@GetMapping("/metadata")
	public ResponseEntity<OhrResponse<?>> getMetadataRecipe() {
		return ResponseEntity.status(SUCCESS.getStatus())
			.body(new OhrResponse<>(recipeService.getMetadataRecipe()));
	}

	/**
	 * 사용자 이름에 따른 레시피 조회
	 */
	@GetMapping("/member/search")
	public ResponseEntity<OhrResponse<?>> getMemberSearchRecipe(
		@RequestParam("nickname") String nickname,
		@RequestParam("page") int page
	) {
		Pageable pageable = PageRequest.of(page, PAGE_SIZE);

		return ResponseEntity.status(SUCCESS.getStatus())
			.body(new OhrResponse<>(recipeService.getMemberSearchRecipe(nickname, pageable)));
	}

	/**
	 * 레시피 이름 조회
	 */
	@GetMapping("/search")
	public ResponseEntity<OhrResponse<?>> getSearchRecipe(
		@RequestParam("name") String name,
		@RequestParam("page") int page
	) {
		Pageable pageable = PageRequest.of(page, PAGE_SIZE);

		return ResponseEntity.status(SUCCESS.getStatus())
			.body(new OhrResponse<>(recipeService.getSearchRecipe(name, pageable)));
	}
}
