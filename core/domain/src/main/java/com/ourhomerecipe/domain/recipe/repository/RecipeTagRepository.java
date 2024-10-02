package com.ourhomerecipe.domain.recipe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ourhomerecipe.domain.recipe.RecipeTag;

public interface RecipeTagRepository extends JpaRepository<RecipeTag, Long>, RecipeTagCustom {
}
