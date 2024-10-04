package com.ourhomerecipe.dto.recipe.response;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor @AllArgsConstructor
public class RecipeDetailResDto {
	private Long recipeId;

	private String recipeName;

	private String recipeImage;

	private List<RecipeTagResDto> tags = new ArrayList<>();

	private List<RecipeIngredientResDto> ingredients = new ArrayList<>();

	private RecipeMemberResDto member;

	@Data
	@NoArgsConstructor @AllArgsConstructor
	public static class RecipeMemberResDto {
		private Long memberId;

		private String memberNickname;

		private String memberProfileImage;
	}
}
