package com.ourhomerecipe.dto.recipe.response;

import java.util.ArrayList;
import java.util.List;

import com.ourhomerecipe.dto.recipe.enums.TagType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@Builder
@NoArgsConstructor @AllArgsConstructor
public class RecipeTagResDto {
	private TagType tagTypeName;

	private List<TagDto> tags = new ArrayList<>();

	@Data
	public static class TagDto {
		private Long tagId;

		private String tagName;
	}
}
