package com.ourhomerecipe.domain.recipe.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ourhomerecipe.dto.recipe.response.RecipeCommentResDto;

public interface RecipeCommentCustom {
	Page<RecipeCommentResDto> getAllByRecipeId(Long recipeId, Pageable pageable);
}
