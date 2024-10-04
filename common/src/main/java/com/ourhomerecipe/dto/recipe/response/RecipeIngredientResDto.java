package com.ourhomerecipe.dto.recipe.response;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;

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

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private BigDecimal ingredientQuantity;

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private String ingredientUnit;
}
