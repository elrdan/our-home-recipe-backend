package com.ourhomerecipe.dto.recipe.enums;

import lombok.Getter;

@Getter
public enum DifficultyType implements Tagable {
	EASY("쉬움"),
	MEDIUM("중간"),
	HARD("어려움");

	private final String label;

	DifficultyType(String label) {
		this.label = label;
	}

	@Override
	public String getTagValue() {
		return label;
	}

	@Override
	public boolean isValid(String value) {
		return false;
	}

}
