package com.ourhomerecipe.domain.recipe.repository;

import static com.ourhomerecipe.domain.ingredient.QIngredient.*;
import static com.ourhomerecipe.domain.ingredient.QUnitType.*;
import static com.ourhomerecipe.domain.recipe.QRecipeIngredient.*;
import static com.ourhomerecipe.domain.recipe.QRecipeRating.*;
import static com.querydsl.core.types.Projections.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ourhomerecipe.domain.ingredient.QUnitType;
import com.ourhomerecipe.domain.recipe.QRecipeIngredient;
import com.ourhomerecipe.dto.recipe.response.RecipeIngredientResDto;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RecipeIngredientRepositoryImpl implements RecipeIngredientCustom {
	private final JPAQueryFactory queryFactory;

	// TODO - ingredient repository 에서 처리해야 함.
	@Override
	public List<RecipeIngredientResDto> getAll() {
		return queryFactory
			.select(fields(RecipeIngredientResDto.class,
				ingredient.id.as("ingredientId"),
				ingredient.name.as("ingredientName")
			))
			.from(ingredient)
			.fetch();
	}

	@Override
	public List<RecipeIngredientResDto> getAllByRecipeId(Long recipeId) {
		return queryFactory
			.select(fields(RecipeIngredientResDto.class,
				ingredient.id.as("ingredientId"),
				ingredient.name.as("ingredientName"),
				recipeIngredient.quantity.as("ingredientQuantity"),
				unitType.name.as("ingredientUnit")
				))
			.from(recipeIngredient)
			.leftJoin(ingredient).on(recipeIngredient.ingredient.id.eq(ingredient.id))
			.leftJoin(unitType).on(ingredient.unitType.id.eq(unitType.id))
			.where(recipeIngredient.recipe.id.eq(recipeId))
			.fetch();
	}
}
