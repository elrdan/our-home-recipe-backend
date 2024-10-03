package com.ourhomerecipe.domain.recipe;

import com.ourhomerecipe.domain.MutableBaseEntity;
import com.ourhomerecipe.dto.recipe.request.RecipeRegisterReqDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Recipe extends MutableBaseEntity {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "recipe_id")
	private Long id;

	private String name;

	@Builder.Default
	private int viewCount = 0;

	private String image;

	@Lob
	private String description;

	public static Recipe fromRecipeRegisterReqDto(RecipeRegisterReqDto registerDto) {
		Recipe recipe = Recipe.builder()
			.name(registerDto.getRecipeName())
			.description(registerDto.getDescription())
			.image("https://our-home-recipe-bucket.s3.ap-northeast-2.amazonaws.com/no-image.jpg")
			.build();

		return recipe;
	}
}
