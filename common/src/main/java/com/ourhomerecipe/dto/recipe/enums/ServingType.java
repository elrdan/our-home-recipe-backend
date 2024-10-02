package com.ourhomerecipe.dto.recipe.enums;

import lombok.Getter;

@Getter
public enum ServingType implements Tagable {
	ONE,        // 1인분
	TWO,        // 2인분
	THREE,      // 3인분
	FOUR,       // 4인분
	FAMILY;     // 가족용 (여러 인분)

	@Override
	public String getTagValue() {
		return this.name();
	}

	@Override
	public boolean isValid(String value) {
		return false;
	}
}
