package com.ourhomerecipe.domain.recipe.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ourhomerecipe.dto.recipe.response.RecipeDetailResDto;
import com.ourhomerecipe.dto.recipe.response.RecipeSearchResDto;

public interface RecipeCustom {
	/**
	 * 레시피 회원 닉네임 조회
	 */
	Page<RecipeSearchResDto> getAllByMemberNickname(String nickname, Pageable pageable);

	/**
	 * 레시피 이름 조회
	 */
	Page<RecipeSearchResDto> getAllByName(String name, Pageable pageable);

	/**
	 * 레시피 상세 조회
	 */
	RecipeDetailResDto getDetailRecipe(Long reicpeId);
}
