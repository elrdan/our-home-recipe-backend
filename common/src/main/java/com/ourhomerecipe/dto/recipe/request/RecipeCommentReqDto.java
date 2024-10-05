package com.ourhomerecipe.dto.recipe.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor @AllArgsConstructor
public class RecipeCommentReqDto {
	@NotBlank(message = "레시피 이름은 필수 항목입니다.")
	private Long recipeId;

	@NotBlank(message = "레시피 코멘트는 필수 항목입니다.")
	private String comment;
}
