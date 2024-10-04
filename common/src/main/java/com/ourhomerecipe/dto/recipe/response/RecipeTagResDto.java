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
public class RecipeTagResDto {
	private String tagTypeName;

	private List<TagDto> tags = new ArrayList<>();

	@Data
	public static class TagDto {
		private Long tagId;

		private String tagName;
	}
}
