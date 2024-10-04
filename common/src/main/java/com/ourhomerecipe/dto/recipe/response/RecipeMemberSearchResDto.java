package com.ourhomerecipe.dto.recipe.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor @AllArgsConstructor
public class RecipeMemberSearchResDto {
	private Long recipeId;

	private String recipeName;

	private Double rating;

	private Integer viewCount;

	private String createBy;
}
