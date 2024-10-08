package com.ourhomerecipe.dto.recipe.enums;

import lombok.Getter;

@Getter
public enum PurposeType implements Tagable {
	DAILY("일상"),
	PARTY("파티"),
	DIET("다이어트"),
	GUEST("손님");

	private final String label;

	PurposeType(String label) {
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
