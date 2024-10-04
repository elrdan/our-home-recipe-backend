package com.ourhomerecipe.domain.recipe.repository;

import java.util.List;

import com.ourhomerecipe.dto.recipe.response.RecipeTagResDto;

public interface RecipeTagCustom {
	// 모든 태그에 타입을 붙여서 반환
	List<RecipeTagResDto> getAll();

	List<RecipeTagResDto> getAllByRecipeId(Long recipeId);
}
