package com.ourhomerecipe.domain.recipe.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ourhomerecipe.dto.recipe.response.RecipeSearchResDto;

public interface RecipeCustom {
	Page<RecipeSearchResDto> getAllMemberSearchRecipe(String nickname, Pageable pageable);

	Page<RecipeSearchResDto> getAllSearchRecipe(String name, Pageable pageable);
}
