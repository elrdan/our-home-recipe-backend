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

import com.ourhomerecipe.domain.member.QMember;
import com.ourhomerecipe.dto.recipe.response.RecipeMemberSearchResDto;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RecipeRepositoryImpl implements RecipeCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<RecipeMemberSearchResDto> getAllMemberRecipe(String nickname, Pageable pageable) {
		List<RecipeMemberSearchResDto> recipeList = queryFactory
			.select(fields(RecipeMemberSearchResDto.class,
				recipe.id.as("recipeId"),
				recipe.name.as("recipeName"),
				Expressions.numberTemplate(Double.class, "AVG({0})", recipeRating.rating).as("rating"),
				recipe.viewCount,
				member.nickname
			))
			.from(recipe)
			.leftJoin(member).on(recipe.createdBy.eq(member.email))
			.leftJoin(recipeRating).on(recipe.id.eq(recipeRating.recipe.id))
			.groupBy(recipe.id, member.nickname, recipe.viewCount)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long totalCount = Optional.ofNullable(queryFactory
			.select(recipe.count())
			.from(recipe)
			.leftJoin(member).on(recipe.createdBy.eq(member.email))
			.leftJoin(recipeRating).on(recipe.id.eq(recipeRating.recipe.id))
			.groupBy(recipe.id, member.nickname, recipe.viewCount)
			.fetchOne()).orElse(0L);

		return new PageImpl<>(recipeList, pageable, totalCount);
	}
}
