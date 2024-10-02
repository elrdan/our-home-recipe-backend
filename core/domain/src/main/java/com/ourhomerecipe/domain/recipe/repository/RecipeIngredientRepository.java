package com.ourhomerecipe.domain.recipe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ourhomerecipe.domain.recipe.RecipeIngredient;

public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Long>, RecipeIngredientCustom {

}
