package com.ourhomerecipe.recipe.controller;

import static com.ourhomerecipe.domain.common.success.code.GlobalSuccessCode.*;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
