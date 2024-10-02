package com.ourhomerecipe.domain.ingredient.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ourhomerecipe.domain.ingredient.Ingredient;

public interface IngredientRepository extends JpaRepository<Ingredient, Long>, IngredientCustom {
	/**
	 * 재료 조회
	 */
	Optional<Ingredient> findById(Long id);
}
