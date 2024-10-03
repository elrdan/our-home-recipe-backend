package com.ourhomerecipe.domain.recipe.repository;

import static com.ourhomerecipe.domain.ingredient.QIngredient.*;
import static com.querydsl.core.types.Projections.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ourhomerecipe.dto.recipe.response.RecipeIngredientResDto;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RecipeIngredientRepositoryImpl implements RecipeIngredientCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<RecipeIngredientResDto> getAllIngredient() {
		return queryFactory
			.select(fields(RecipeIngredientResDto.class,
				ingredient.id.as("ingredientId"),
				ingredient.name.as("ingredientName")
			))
			.from(ingredient)
			.fetch();
	}
}
