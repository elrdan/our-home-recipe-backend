package com.ourhomerecipe.domain.ingredient.enums;

import lombok.Getter;

@Getter
public enum UnitType {
	// 무게(Mass)
	GRAM("g"),					// 작은 재료나 향신료의 양을 측정할 때 사용
	KILOGRAM("kg"),				// 더 큰 양의 식재료를 측정할 때 사용

	// 부피(Volume)
	MILLILITER("ml"),			// 액체 재료의 양을 측정할 때 주로 사용
	LITER("l"),					// 액체 재료의 더 큰 양을 측정할 때 사용
	TEASPOON("tsp"),			// 소량의 액체나 분말 형태의 재료를 측정할 때 사용
	TABLESPOON("tbsp"),			// 티스푼보다 조금 더 많은 양을 측정할 때 사용
	CUP("c"),					// 조리에 사용되는 다양한 재료의 양을 측정할 때 사용

	// 수량(Count)
	PIECE("ea"),				// 별적인 아이템의 수를 세는 데 사용
	BUNCH("bunch"),				// 묶음 단위로 판매되는 식재료, 예를 들어 파나 바나나 등을 측정할 때 사용
	SLICE("slice"),				// 잘라진 식재료의 조각 수를 나타냄

	// 중량(Weight)
	POUND("lb"),				// 미국에서 주로 사용하는 무게 단위
	OUNCE("oz"),				// 파운드보다 작은 무게를 측정할 때 사용

	// 길이(Length)
	CENTIMETER("cm"),			// 식재료의 길이를 측정할 때 사용
	INCH("in");					// 길이 측정에 사용되는 또 다른 단위로, 주로 미국에서 사용

	private final String abbreviation;

	UnitType(String abbreviation) {
		this.abbreviation = abbreviation;
	}
}
