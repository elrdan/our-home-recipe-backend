package com.ourhomerecipe.dto.recipe.utils;

import com.ourhomerecipe.dto.recipe.enums.CookingTime;
import com.ourhomerecipe.dto.recipe.enums.DifficultyType;
import com.ourhomerecipe.dto.recipe.enums.MealTimeType;
import com.ourhomerecipe.dto.recipe.enums.PurposeType;
import com.ourhomerecipe.dto.recipe.enums.SeasonType;
import com.ourhomerecipe.dto.recipe.enums.ServingType;
import com.ourhomerecipe.dto.recipe.enums.TagType;
import com.ourhomerecipe.dto.recipe.enums.Tagable;

public class TagTypeUtil {
	public static Class<? extends Tagable> getTagableClassForTagType(TagType tagType) {
		switch (tagType) {
			case MEAL_TIME:
				return MealTimeType.class;
			case PURPOSE:
				return PurposeType.class;
			case SEASON:
				return SeasonType.class;
			case SERVING:
				return ServingType.class;
			case DIFFICULTY:
				return DifficultyType.class;
			case COOKING_TIME:
				return CookingTime.class;
			// 다른 케이스 추가
			default:
				throw new IllegalArgumentException("지원하지 않는 태그입니다.");
		}
	}
}
