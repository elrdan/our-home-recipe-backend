package com.ourhomerecipe.dto.recipe.response;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeMetadataResDto {
	List<RecipeIngredientResDto> ingredients = new ArrayList<>();

	List<RecipeTagResDto> tags = new ArrayList<>();
}
