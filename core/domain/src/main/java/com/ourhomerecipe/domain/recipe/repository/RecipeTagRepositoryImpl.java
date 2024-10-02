package com.ourhomerecipe.domain.recipe.repository;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RecipeTagRepositoryImpl implements RecipeTagCustom {
	private final JPAQueryFactory queryFactory;
}
