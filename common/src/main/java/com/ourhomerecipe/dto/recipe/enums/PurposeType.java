package com.ourhomerecipe.dto.recipe.enums;

import lombok.Getter;

@Getter
public enum PurposeType implements Tagable {
	DAILY,      // 일상
	PARTY,      // 파티용
	DIET,       // 다이어트
	GUEST;      // 손님접대

	@Override
	public String getTagValue() {
		return this.name();
	}

	@Override
	public boolean isValid(String value) {
		return false;
	}
}
