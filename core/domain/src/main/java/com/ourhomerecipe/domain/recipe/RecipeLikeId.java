package com.ourhomerecipe.domain.recipe;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Embeddable
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeLikeId {
	private Long recipeId;

	private Long memberId;
}
