package com.ourhomerecipe.domain.recipe.repository;

import static com.ourhomerecipe.domain.tag.QTag.*;
import static com.ourhomerecipe.domain.tag.QTagType.*;
import static com.querydsl.core.types.Projections.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.ourhomerecipe.dto.recipe.enums.TagType;
import com.ourhomerecipe.dto.recipe.response.RecipeTagResDto;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RecipeTagRepositoryImpl implements RecipeTagCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<RecipeTagResDto> getAllTagAndType() {
		// 태그 타입별로 그룹핑
		Map<TagType, RecipeTagResDto> tagTypeMap = new HashMap<>();

		// 프로젝션을 사용하여 TagDto와 TagTypeName을 매핑
		queryFactory
			.select(
				fields(
					RecipeTagResDto.class,
					tagType.name.as("tagTypeName") // TagTypeName을 직접 가져옴
				),
				fields(
					RecipeTagResDto.TagDto.class,
					tag.id.as("tagId"),
					tag.name.as("tagName")
				))
			.from(tag)
			.join(tag.tagType, tagType)
			.fetch()
			.forEach(tuple -> {
				// TagTypeName 추출
				RecipeTagResDto tagResDto = tuple.get(0, RecipeTagResDto.class);
				RecipeTagResDto recipeTagResDto = tagTypeMap.get(tagResDto.getTagTypeName());

				// 해당 TagTypeName이 처음 발견되면 새로운 DTO 생성
				if (recipeTagResDto == null) {
					recipeTagResDto = new RecipeTagResDto();
					recipeTagResDto.setTagTypeName(tagResDto.getTagTypeName());
					recipeTagResDto.setTags(new ArrayList<>());
					tagTypeMap.put(tagResDto.getTagTypeName(), recipeTagResDto);
				}

				// TagDto를 추가
				RecipeTagResDto.TagDto tagDto = tuple.get(1, RecipeTagResDto.TagDto.class); // 두 번째 항목에서 TagDto를 가져옴
				recipeTagResDto.getTags().add(tagDto);
			});

		return new ArrayList<>(tagTypeMap.values());
	}
}
