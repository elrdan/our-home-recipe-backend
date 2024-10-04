package com.ourhomerecipe.dto.recipe.enums;

import lombok.Getter;

@Getter
public enum SeasonType implements Tagable {
	SPRING("봄"),
	SUMMER("여름"),
	AUTUMN("가을"),
	WINTER("겨울");

	private final String label;

	SeasonType(String label) {
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
