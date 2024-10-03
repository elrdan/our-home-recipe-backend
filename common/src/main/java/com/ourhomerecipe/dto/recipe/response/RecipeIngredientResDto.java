package com.ourhomerecipe.dto.recipe.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor @AllArgsConstructor
public class RecipeIngredientResDto {
	private Long ingredientId;

	private String ingredientName;
}
