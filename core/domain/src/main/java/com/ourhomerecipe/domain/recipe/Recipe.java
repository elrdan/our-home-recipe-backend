package com.ourhomerecipe.domain.recipe;

import com.ourhomerecipe.domain.MutableBaseEntity;
import com.ourhomerecipe.domain.recipe.enums.DifficultyType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Recipe extends MutableBaseEntity {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "recipe_id")
	private Long id;

	private String name;

	@Lob
	private String description;

	@Enumerated(EnumType.STRING)
	private DifficultyType difficulty;

	private int cookTime;

	private double servings;

	private int viewCount;

	private String image;
}
