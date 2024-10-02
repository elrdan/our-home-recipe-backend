package com.ourhomerecipe.domain.recipe;

import java.math.BigDecimal;

import com.ourhomerecipe.domain.ingredient.Ingredient;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor @AllArgsConstructor
public class RecipeIngredient {
	@EmbeddedId
	private RecipeIngredientId id;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("recipeId")
	@JoinColumn(name = "recipe_id")
	private Recipe recipe;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("ingredientId")
	@JoinColumn(name = "ingredient_id")
	private Ingredient ingredient;

	// 정밀도, 오차없는 연산을 확보하기 위해 BigDecimal 사용
	@Column(precision = 10, scale = 2)
	private BigDecimal quantity;
}
