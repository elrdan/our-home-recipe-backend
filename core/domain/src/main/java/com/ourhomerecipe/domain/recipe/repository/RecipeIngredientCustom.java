package com.ourhomerecipe.domain.recipe.repository;

import java.util.List;

import com.ourhomerecipe.dto.recipe.response.RecipeIngredientResDto;

public interface RecipeIngredientCustom {
	List<RecipeIngredientResDto> getAll();

	List<RecipeIngredientResDto> getAllByRecipeId(Long recipeId);
}
