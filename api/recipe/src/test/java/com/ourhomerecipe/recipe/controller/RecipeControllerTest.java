package com.ourhomerecipe.recipe.controller;

import static com.epages.restdocs.apispec.RestAssuredRestDocumentationWrapper.*;
import static io.restassured.RestAssured.*;
import static io.restassured.http.ContentType.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.restdocs.RestDocumentationExtension;

import com.ourhomerecipe.dto.recipe.enums.TagType;
import com.ourhomerecipe.dto.recipe.request.IngredientReqDto;
import com.ourhomerecipe.dto.recipe.request.RecipeRegisterReqDto;
import com.ourhomerecipe.dto.recipe.request.TagReqDto;
import com.ourhomerecipe.infra.config.BaseTest;

@ExtendWith(RestDocumentationExtension.class)
@TestMethodOrder(value = MethodOrderer.DisplayName.class)
public class RecipeControllerTest extends BaseTest {
	private String accessToken = "eyJhbGciOiJIUzUxMiJ9.eyJpZCI6MSwic3ViIjoiYWRtaW5AZ21haWwuY29tIiwiaWF0IjoxNzI3OTAyNDM1LCJleHAiOjE3NTk1MjQ4MzUsInJvbGVzIjoiUk9MRV9BRE1JTiJ9.YYbi8q5Sj2z7WU4zk9yCvRCJnNP4v7VP6BTI4gx3eqb76OEA5zCfJ0LGHBHIaFaILwZTGigoCbpdYKAgWY9Q-g";

	@Test
	@DisplayName("1-1. 레시피 등록")
	void registerRecipeSuccess() {
		TagReqDto tagReqDto = TagReqDto.builder()
			.id(12L)
			.type(TagType.SERVING)
			.name("ONE")
			.build();

		List<TagReqDto> tags = new ArrayList<>();
		tags.add(tagReqDto);

		IngredientReqDto ingredientReqDto = IngredientReqDto.builder()
			.id(5L)
			.name("돼지고기")
			.quantity("100")
			.build();

		List<IngredientReqDto> ingredients = new ArrayList<>();
		ingredients.add(ingredientReqDto);

		RecipeRegisterReqDto requestDto = RecipeRegisterReqDto.builder()
			.recipeName("레시피 테스트")
			.description("레시피 테스트 설명")
			.tags(tags)
			.ingredients(ingredients)
			.build();

		// 레시피 등록 API 문서화
		given(spec)
			.header("Authorization", "Bearer " + accessToken)
			.filter(document("레시피 등록 API",
				resourceDetails()
					.tag("레시피 API")
					.summary("레시피 등록"),
				// 요청 필드 문서화
				requestFields(
					fieldWithPath("recipeName").type(STRING).description("레시피 이름"),
					fieldWithPath("description").type(STRING).description("레시피 설명"),
					fieldWithPath("tags").type(ARRAY).description("태그 리스트"),
					fieldWithPath("tags[].id").type(NUMBER).description("태그 ID"),
					fieldWithPath("tags[].type").type(STRING).description("태그 타입"),
					fieldWithPath("tags[].name").type(STRING).description("태그 이름"),
					fieldWithPath("ingredients").type(ARRAY).description("재료 리스트"),
					fieldWithPath("ingredients[].id").type(NUMBER).description("재료 ID"),
					fieldWithPath("ingredients[].name").type(STRING).description("재료 타입"),
					fieldWithPath("ingredients[].quantity").type(STRING).description("재료 수량")
				),
				// 응답 필드 문서화
				responseFields(
					fieldWithPath("code").type(NUMBER).description("상태 코드"),
					fieldWithPath("message").type(STRING).description("상태 메시지"),
					fieldWithPath("data.id").type(NUMBER).description("레시피 고유번호")
				)))
			.contentType(JSON)  // 요청 본문의 Content-Type 설정 (JSON 형식)
			.body(requestDto)                // 요청 본문 데이터 설정
		.when()
			.post("/recipe/register")
		.then().log().all()  // 요청 및 응답 로그 출력
			.statusCode(201);
	}
}