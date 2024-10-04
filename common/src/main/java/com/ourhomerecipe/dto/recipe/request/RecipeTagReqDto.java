package com.ourhomerecipe.dto.recipe.request;

import com.ourhomerecipe.dto.recipe.annotation.ValidTag;
import com.ourhomerecipe.dto.recipe.enums.TagType;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ValidTag(message = "등록되지 않은 태그입니다.")
public class RecipeTagReqDto {
	@NotNull(message = "태그 식별자 값은 필수입니다.")
	private Long tagId;

	private String tagName;

	private String tagTypeName;
}
