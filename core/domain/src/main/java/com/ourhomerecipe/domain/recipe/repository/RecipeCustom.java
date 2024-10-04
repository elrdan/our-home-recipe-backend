package com.ourhomerecipe.domain.recipe.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ourhomerecipe.dto.recipe.response.RecipeMemberSearchResDto;

public interface RecipeCustom {
	Page<RecipeMemberSearchResDto> getAllMemberRecipe(String nickname, Pageable pageable);
}
