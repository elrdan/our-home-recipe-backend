package com.ourhomerecipe.dto.recipe.enums;

public class CookingTime implements Tagable {
	private Integer minutes;

	public CookingTime(Integer minutes) {
		this.minutes = minutes;
	}

	@Override
	public String getTagValue() {
		return String.valueOf(this.minutes);
	}

	@Override
	public boolean isValid(String value) {
		try {
			Integer.parseInt(value);
			return true;
		}catch (NumberFormatException e) {
			return false;
		}
	}
}
