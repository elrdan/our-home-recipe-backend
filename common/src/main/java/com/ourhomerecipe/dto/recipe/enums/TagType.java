package com.ourhomerecipe.dto.recipe.enums;

import java.util.Arrays;

import lombok.Getter;

@Getter
public enum TagType {
	METHOD("방식"),
	INGREDIENT("재료"),
	MEAL_TIME("식사 시간"),
	REGION("지역"),
	PURPOSE("목적"),
	COOKING_TIME("조리 시간"),
	SEASON("계절"),
	SERVING("인분"),
	DIFFICULTY("난이도");

	private final String label;

	TagType(String label) {
		this.label = label;
	}

	// label 값을 통해 TagType enum을 반환하는 메서드
	public static TagType fromLabel(String label) {
		return Arrays.stream(TagType.values())
			.filter(tagType -> tagType.getLabel().equals(label))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("No enum constant for label: " + label));
	}
}