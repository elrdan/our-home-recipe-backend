package com.ourhomerecipe.domain.recipe;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Embeddable
@Builder
@NoArgsConstructor @AllArgsConstructor
public class RecipeTagId implements Serializable {
	private Long recipeId;

	private Long tagId;
}
