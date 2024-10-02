package com.ourhomerecipe.domain.ingredient.repository;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class IngredientRepositoryImpl implements IngredientCustom {
	private final JPAQueryFactory queryFactory;
}
