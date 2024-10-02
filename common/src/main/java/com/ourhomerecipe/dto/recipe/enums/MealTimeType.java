package com.ourhomerecipe.dto.recipe.enums;

import lombok.Getter;

@Getter
public enum MealTimeType implements Tagable {
	BREAKFAST,				// 아침
	LUNCH,					// 점심
	DINNER,					// 저녁
	SNACK;					// 간식

	@Override
	public String getTagValue() {
		return this.name();
	}

	@Override
	public boolean isValid(String value) {
		return false;
	}

}
