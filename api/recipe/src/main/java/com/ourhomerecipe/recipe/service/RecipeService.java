package com.ourhomerecipe.recipe.service;

import static com.ourhomerecipe.domain.common.error.code.RecipeErrorCode.*;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import com.ourhomerecipe.dto.recipe.request.RecipeIngredientReqDto;
import com.ourhomerecipe.dto.recipe.request.RecipeRegisterReqDto;
import com.ourhomerecipe.dto.recipe.request.RecipeTagReqDto;
import com.ourhomerecipe.dto.recipe.response.RecipeIngredientResDto;
import com.ourhomerecipe.dto.recipe.response.RecipeSearchResDto;
import com.ourhomerecipe.dto.recipe.response.RecipeMetadataResDto;
import com.ourhomerecipe.dto.recipe.response.RecipeTagResDto;
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
			for(RecipeIngredientReqDto ingredientData: recipeRegisterReqDto.getIngredients()) {
				Ingredient ingredient = entityManager.getReference(Ingredient.class, ingredientData.getIngredientId());

				RecipeIngredientId recipeIngredientId = RecipeIngredientId.builder()
					.recipeId(recipe.getId())
					.ingredientId(ingredient.getId())
					.build();

				RecipeIngredient recipeIngredient = RecipeIngredient.builder()
					.id(recipeIngredientId)
					.recipe(recipe)
					.ingredient(ingredient)
					.quantity(new BigDecimal(ingredientData.getIngredientQuantity()))
					.build();

				recipeIngredientRepository.save(recipeIngredient);
			}
		}catch (EntityNotFoundException e) {
			throw new RecipeException(NOT_REGISTER_INGREDIENT);
		}

		// 레시피, 태그 관계 저장
		try {
			for(RecipeTagReqDto tagData: recipeRegisterReqDto.getTags()) {
				Tag tag = entityManager.getReference(Tag.class, tagData.getTagId());

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

	/**
	 * 레시피 메타 데이터 조회
	 */
	@Transactional(readOnly = true)
	public RecipeMetadataResDto getMetadataRecipe() {
		List<RecipeIngredientResDto> ingredients = recipeIngredientRepository.getAllIngredient();
		List<RecipeTagResDto> tagList = recipeTagRepository.getAllTagAndType();

		return RecipeMetadataResDto.builder()
			.ingredients(ingredients)
			.tags(tagList)
			.build();
	}

	/**
	 * 레시피 회원 닉네임 조회
	 */
	@Transactional(readOnly = true)
	public Page<RecipeSearchResDto> getMemberSearchRecipe(String nickname, Pageable pageable) {
		return recipeRepository.getAllMemberSearchRecipe(nickname, pageable);
	}

	/**
	 * 레시피 이름 조회
	 */
	public Page<RecipeSearchResDto> getSearchRecipe(String name, Pageable pageable) {
		return recipeRepository.getAllSearchRecipe(name, pageable);
	}
}
