package com.ourhomerecipe.domain.recipe.repository;

import static com.ourhomerecipe.domain.recipe.QRecipeComment.*;
import static com.querydsl.core.types.Projections.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.ourhomerecipe.dto.recipe.response.RecipeCommentResDto;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RecipeCommentRepositoryImpl implements RecipeCommentCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<RecipeCommentResDto> getAllByRecipeId(Long recipeId, Pageable pageable) {
		List<RecipeCommentResDto> commentList = queryFactory
			.select(fields(RecipeCommentResDto.class,
				recipeComment.id.as("commentId"),
				recipeComment.comment,
				recipeComment.createdBy,
				recipeComment.createdAt,
				recipeComment.updatedAt
			))
			.from(recipeComment)
			.where(recipeComment.recipe.id.eq(recipeId))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long totalCount = Optional.ofNullable(queryFactory
			.select(recipeComment.count())
			.from(recipeComment)
			.where(recipeComment.recipe.id.eq(recipeId))
			.fetchOne()).orElse(0L);

		return new PageImpl<>(commentList, pageable, totalCount);
	}
}
