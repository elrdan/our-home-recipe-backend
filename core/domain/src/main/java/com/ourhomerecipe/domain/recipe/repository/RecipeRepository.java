package com.ourhomerecipe.domain.recipe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ourhomerecipe.domain.recipe.Recipe;

public interface RecipeRepository extends JpaRepository<Recipe, Long>, RecipeCustom {

}
