package com.ourhomerecipe.domain.recipe.repository;

import static com.ourhomerecipe.domain.recipe.QRecipeTag.*;
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
	public List<RecipeTagResDto> getAll() {
		// 태그 타입별로 그룹핑
		Map<String, RecipeTagResDto> tagTypeMap = new HashMap<>();

		// 프로젝션을 사용하여 TagDto와 TagTypeName을 매핑
		queryFactory
			.select(
				tagType.name.as("tagTypeName"), // enum 상수 이름 가져옴
				fields(
					RecipeTagResDto.TagDto.class,
					tag.id.as("tagId"),
					tag.name.as("tagName")
				))
			.from(tag)
			.join(tag.tagType, tagType)
			.fetch()
			.forEach(tuple -> {
				// TagType 상수 이름을 가져옴
				String tagTypeName = tuple.get(0, String.class);

				RecipeTagResDto recipeTagResDto = tagTypeMap.get(tagTypeName); // label을 사용

				// 해당 TagTypeName이 처음 발견되면 새로운 DTO 생성
				if (recipeTagResDto == null) {
					recipeTagResDto = new RecipeTagResDto();
					recipeTagResDto.setTagTypeName(tagTypeName); // label 값 설정
					recipeTagResDto.setTags(new ArrayList<>());
					tagTypeMap.put(tagTypeName, recipeTagResDto);
				}

				// TagDto를 추가
				RecipeTagResDto.TagDto tagDto = tuple.get(1, RecipeTagResDto.TagDto.class);
				recipeTagResDto.getTags().add(tagDto);
			});

		return new ArrayList<>(tagTypeMap.values());
	}

	@Override
	public List<RecipeTagResDto> getAllByRecipeId(Long recipeId) {
		// 태그 타입별로 그룹핑
		Map<String, RecipeTagResDto> tagTypeMap = new HashMap<>();

		queryFactory
			.select(
				tagType.name.as("tagTypeName"),
				fields(RecipeTagResDto.TagDto.class,
					tag.id.as("tagId"),
					tag.name.as("tagName")
				)
			)
			.from(recipeTag)
			.where(recipeTag.recipe.id.eq(recipeId))
			.leftJoin(tag).on(recipeTag.tag.id.eq(tag.id))
			.leftJoin(tagType).on(tag.tagType.id.eq(tagType.id))
			.fetch()
			.forEach(tuple -> {
				String tagTypeName = tuple.get(0, String.class);

				RecipeTagResDto recipeTagResDto = tagTypeMap.get(tagTypeName);

				if(recipeTagResDto == null) {
					recipeTagResDto = new RecipeTagResDto();
					recipeTagResDto.setTagTypeName(tagTypeName);
					recipeTagResDto.setTags(new ArrayList<>());
					tagTypeMap.put(tagTypeName, recipeTagResDto);
				}

				// TagDto를 추가
				RecipeTagResDto.TagDto tagDto = tuple.get(1, RecipeTagResDto.TagDto.class);
				recipeTagResDto.getTags().add(tagDto);
			});

		return new ArrayList<>(tagTypeMap.values());
	}
}
