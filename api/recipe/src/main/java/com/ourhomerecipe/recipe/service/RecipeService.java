package com.ourhomerecipe.recipe.service;

import static com.ourhomerecipe.domain.common.error.code.RecipeErrorCode.*;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ourhomerecipe.domain.ingredient.Ingredient;
import com.ourhomerecipe.domain.recipe.Recipe;
import com.ourhomerecipe.domain.recipe.RecipeIngredient;
import com.ourhomerecipe.domain.recipe.RecipeIngredientId;
import com.ourhomerecipe.domain.recipe.RecipeTag;
import com.ourhomerecipe.domain.recipe.RecipeTagId;
import com.ourhomerecipe.domain.recipe.repository.RecipeIngredientRepository;
import com.ourhomerecipe.domain.recipe.repository.RecipeRepository;
import com.ourhomerecipe.domain.recipe.repository.RecipeTagRepository;
import com.ourhomerecipe.domain.tag.Tag;
import com.ourhomerecipe.dto.recipe.request.IngredientReqDto;
import com.ourhomerecipe.dto.recipe.request.RecipeRegisterReqDto;
import com.ourhomerecipe.dto.recipe.request.TagReqDto;
import com.ourhomerecipe.recipe.exception.RecipeException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeService {
	private final EntityManager entityManager;
	private final RecipeRepository recipeRepository;
	private final RecipeIngredientRepository recipeIngredientRepository;
	private final RecipeTagRepository recipeTagRepository;

	/**
	 * 레시피 등록
	 */
	@Transactional
	public Recipe registerRecipe(
		RecipeRegisterReqDto recipeRegisterReqDto
	) {
		// 레시피 생성
		Recipe recipe = Recipe.fromRecipeRegisterReqDto(recipeRegisterReqDto);

		recipeRepository.save(recipe);

		// 레시피, 재료 관계 저장
		try {
			for(IngredientReqDto ingredientData: recipeRegisterReqDto.getIngredients()) {
				Ingredient ingredient = entityManager.getReference(Ingredient.class, ingredientData.getId());

				RecipeIngredientId recipeIngredientId = RecipeIngredientId.builder()
					.recipeId(recipe.getId())
					.ingredientId(ingredient.getId())
					.build();

				RecipeIngredient recipeIngredient = RecipeIngredient.builder()
					.id(recipeIngredientId)
					.recipe(recipe)
					.ingredient(ingredient)
					.quantity(new BigDecimal(ingredientData.getQuantity()))
					.build();

				recipeIngredientRepository.save(recipeIngredient);
			}
		}catch (EntityNotFoundException e) {
			throw new RecipeException(NOT_REGISTER_INGREDIENT);
		}

		// 레시피, 태그 관계 저장
		try {
			for(TagReqDto tagData: recipeRegisterReqDto.getTags()) {
				Tag tag = entityManager.getReference(Tag.class, tagData.getId());

				RecipeTagId recipeTagId = RecipeTagId.builder()
					.recipeId(recipe.getId())
					.tagId(tag.getId())
					.build();

				RecipeTag recipeTag = RecipeTag.builder()
					.id(recipeTagId)
					.recipe(recipe)
					.tag(tag)
					.build();

				recipeTagRepository.save(recipeTag);
			}
		}catch (EntityNotFoundException e) {
			throw new RecipeException(NOT_REGISTER_TAG);
		}

		return recipe;
	}
}
