package com.ourhomerecipe.dto.recipe.enums;

import lombok.Getter;

@Getter
public enum MealTimeType implements Tagable {
	BREAKFAST("아침"),
	LUNCH("점심"),
	DINNER("저녁"),
	SNACK("간식");

	private final String label;

	MealTimeType(String label) {
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
