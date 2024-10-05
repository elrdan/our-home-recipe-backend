package com.ourhomerecipe.domain.recipe.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ourhomerecipe.domain.recipe.RecipeComment;

public interface RecipeCommentRepository extends JpaRepository<RecipeComment, Long>, RecipeCommentCustom {
}
