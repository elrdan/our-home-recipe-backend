package com.ourhomerecipe.dto.recipe.enums;

public interface Tagable {
	String getTagValue();

	boolean isValid(String value);
}
