package com.ourhomerecipe.domain.recipe.repository;

import static com.ourhomerecipe.domain.member.QMember.*;
import static com.ourhomerecipe.domain.recipe.QRecipe.*;
import static com.ourhomerecipe.domain.recipe.QRecipeRating.*;
import static com.querydsl.core.types.Projections.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.ourhomerecipe.dto.recipe.response.RecipeSearchResDto;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RecipeRepositoryImpl implements RecipeCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<RecipeSearchResDto> getAllMemberSearchRecipe(String nickname, Pageable pageable) {
		List<RecipeSearchResDto> recipeList = queryFactory
			.select(fields(RecipeSearchResDto.class,
				recipe.id.as("recipeId"),
				recipe.name.as("recipeName"),
				Expressions.numberTemplate(Double.class, "COALESCE(AVG({0}), 0)", recipeRating.rating).as("rating"),
				recipe.viewCount,
				member.nickname.as("createdBy")
			))
			.from(recipe)
			.leftJoin(member).on(recipe.createdBy.eq(member.email))
			.leftJoin(recipeRating).on(recipe.id.eq(recipeRating.recipe.id))
			.where(member.nickname.startsWith(nickname))
			.groupBy(recipe.id, member.nickname, recipe.viewCount)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long totalCount = Optional.ofNullable(queryFactory
			.select(recipe.count())
			.from(recipe)
			.leftJoin(member).on(recipe.createdBy.eq(member.email))
			.leftJoin(recipeRating).on(recipe.id.eq(recipeRating.recipe.id))
			.where(member.nickname.startsWith(nickname))
			.groupBy(recipe.id, member.nickname, recipe.viewCount)
			.fetchOne()).orElse(0L);

		return new PageImpl<>(recipeList, pageable, totalCount);
	}

	@Override
	public Page<RecipeSearchResDto> getAllSearchRecipe(String name, Pageable pageable) {
		List<RecipeSearchResDto> recipeList = queryFactory
			.select(fields(RecipeSearchResDto.class,
				recipe.id.as("recipeId"),
				recipe.name.as("recipeName"),
				Expressions.numberTemplate(Double.class, "COALESCE(AVG({0}), 0)", recipeRating.rating).as("rating"),
				recipe.viewCount,
				member.nickname.as("createdBy")
			))
			.from(recipe)
			.leftJoin(member).on(recipe.createdBy.eq(member.email))
			.leftJoin(recipeRating).on(recipe.id.eq(recipeRating.recipe.id))
			.where(recipe.name.startsWith(name))
			.groupBy(recipe.id, member.nickname, recipe.viewCount)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long totalCount = Optional.ofNullable(queryFactory
			.select(recipe.count())
			.from(recipe)
			.leftJoin(member).on(recipe.createdBy.eq(member.email))
			.leftJoin(recipeRating).on(recipe.id.eq(recipeRating.recipe.id))
			.where(recipe.name.startsWith(name))
			.groupBy(recipe.id, member.nickname, recipe.viewCount)
			.fetchOne()).orElse(0L);

		return new PageImpl<>(recipeList, pageable, totalCount);
	}
}
