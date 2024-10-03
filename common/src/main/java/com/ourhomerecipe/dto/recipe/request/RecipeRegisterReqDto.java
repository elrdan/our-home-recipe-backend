package com.ourhomerecipe.dto.recipe.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor @AllArgsConstructor
public class RecipeRegisterReqDto {
	@NotBlank(message = "레시피 이름은 필수 항목입니다.")
	@Size(max = 12, message = "레시피 이름은 12자 이하로 입력해주세요.")
	private String recipeName;

	@NotBlank(message = "레시피 설명은 필수 항목입니다.")
	private String description;

	@Valid
	@NotEmpty(message = "하나 이상의 태그가 필요합니다.")
	private List<RecipeTagReqDto> tags;

	@Valid
	@NotEmpty(message = "하나 이상의 재료가 필요합니다.")
	private List<RecipeIngredientReqDto> ingredients;
}
