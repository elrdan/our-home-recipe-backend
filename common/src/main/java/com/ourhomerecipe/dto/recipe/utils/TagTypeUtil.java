package com.ourhomerecipe.dto.recipe.utils;

import static com.ourhomerecipe.dto.recipe.enums.TagType.*;

import com.ourhomerecipe.dto.recipe.enums.CookingTime;
import com.ourhomerecipe.dto.recipe.enums.DifficultyType;
import com.ourhomerecipe.dto.recipe.enums.MealTimeType;
import com.ourhomerecipe.dto.recipe.enums.PurposeType;
import com.ourhomerecipe.dto.recipe.enums.SeasonType;
import com.ourhomerecipe.dto.recipe.enums.ServingType;
import com.ourhomerecipe.dto.recipe.enums.TagType;
import com.ourhomerecipe.dto.recipe.enums.Tagable;

public class TagTypeUtil {
	public static Class<? extends Tagable> getTagableClassForTagType(String tagType) {
		switch (tagType) {
			case "식사 시간":
				return MealTimeType.class;
			case "목적":
				return PurposeType.class;
			case "계절":
				return SeasonType.class;
			case "인분":
				return ServingType.class;
			case "난이도":
				return DifficultyType.class;
			case "조리 시간":
				return CookingTime.class;
			// 다른 케이스 추가
			default:
				throw new IllegalArgumentException("지원하지 않는 태그입니다.");
		}
	}
}
