/**
 * 이 파일은 Spring REST Docs와 OpenAPI를 사용한 API 테스트 작성 예시입니다.
 * 실제 프로젝트에서 API 테스트를 작성할 때 이 템플릿을 참고하세요.
 *
 * 사용 방법:
 * 1. RecipeController, RecipeService, Recipe 등의 클래스를 프로젝트의 실제 클래스로 교체하세요.
 * 2. 테스트 메소드(testGetRecipe, testGetRecipeNotFound)를 실제 API 엔드포인트에 맞게 수정하세요.
 * 3. 필요한 경우 추가적인 테스트 메소드를 작성하세요.
 * 4. 프로젝트의 요구사항에 맞춰 문서화할 필드나 파라미터를 조정하세요.
 */

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;

import org.mockito.BDDMockito;

// @WebMvcTest: 웹 계층 테스트에 필요한 빈들만 로드하여 테스트 속도를 향상
// RecipeController.class: 테스트 대상 컨트롤러를 지정
@WebMvcTest(RecipeController.class)

// @ExtendWith: JUnit 5의 확장 기능을 사용.
// RestDocumentationExtension.class: Spring REST Docs 기능을 JUnit 5에서 사용하기 위한 확장
@ExtendWith(RestDocumentationExtension.class)

public class RecipeControllerTest {

    // MockMvc: 실제 서버를 구동하지 않고 Spring MVC의 동작을 재현하는 테스트용 객체
    @Autowired
    private MockMvc mockMvc;

    // @MockBean: Spring 컨텍스트에 모의 객체를 추가.
    // 1. 컨트롤러 로직만 단독으로 테스트 가능 (서비스 로직 분리)
    // 2. 데이터베이스 조회 없이 빠른 테스트 실행
    @MockBean
    private RecipeService recipeService;

    // @BeforeEach: 각 테스트 메소드 실행 전에 실행되는 설정 메소드
    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentation) {
        // MockMvc 빌더를 사용하여 REST Docs 설정을 적용한 MockMvc 인스턴스를 생성
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentation))
                .build();
//        .alwaysDo(MockMvcRestDocumentation.document(...))   // 모든 테스트에 대하 문서화를 할 것인지 결정
    }

    // @Test: JUnit 테스트 메소드
    @Test
    public void testGetRecipe() throws Exception {
        // Given
        // 테스트에 사용할 가상의 Recipe 객체를 생성합니다.
        Recipe recipe = new Recipe(1L, "참치 김치찌개", "참치를 곁들인 김치찌개");

        // Mockito를 사용하여 서비스 계층의 동작을 정의
        // recipeService.getRecipe(1L) 호출 시 위에서 생성한 recipe 객체를 반환하도록 설정
        BDDMockito.given(recipeService.getRecipe(1L)).willReturn(recipe);

        // When
        // mockMvc를 사용하여 GET 요청을 수행
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/api/recipes/{recipe_id}", 1L)
                .accept(MediaType.APPLICATION_JSON));

        // Then
        result.andExpect(MockMvcResultMatchers.status().isOk())  // HTTP 상태 코드가 200 OK인지 확인
                .andExpect(MockMvcResultMatchers.jsonPath("$.recipe_id").value(1))  // JSON 응답의 id 필드가 1인지 확인
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("참치 김치찌개"))  // name 필드 확인
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("참치를 곁들인 김치찌개"))  // description 필드 확인
                .andDo(MockMvcRestDocumentationWrapper.document("get-recipe",  // REST Docs와 OpenAPI 스펙을 함께 생성
                        ResourceSnippetParameters.builder()  // OpenAPI 스펙에 포함될 정보를 빌더로 구성
                                .summary("레시피 조회")  // API 요약 정보
                                .description("ID로 특정 레시피를 조회합니다.")  // API 상세 설명
                                .pathParameters(  // URL 경로 변수를 문서화
                                        RequestDocumentation.parameterWithName("id").description("레시피 ID")
                                )
                    ㅌ            .responseFields(  // 응답 필드를 문서화
                                        PayloadDocumentation.fieldWithPath("recipe_id").description("레시피 ID"),
                                        PayloadDocumentation.fieldWithPath("name").description("레시피 이름"),
                                        PayloadDocumentation.fieldWithPath("description").description("레시피 소개")
                                )
                                .responseSchema(Schema.schema("Recipe"))  // 응답 스키마 지정 (Swagger UI에서 모델로 표시됨)
                                .build()
                ));
        // 이 테스트는 API 호출, 응답 검증, API 문서 생성, OpenAPI 스펙 생성을 한 번에 수행
    }

    @Test
    public void testGetRecipeNotFound() throws Exception {
        // Given
        // 존재하지 않는 레시피 ID로 요청 시 예외 발생을 모의 설정
        BDDMockito.given(recipeService.getRecipe(99L)).willThrow(new RecipeNotFoundException("레시피를 찾을 수 없습니다"));

        // When
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/api/recipes/{id}", 99L)
                .accept(MediaType.APPLICATION_JSON));

        // Then
        result.andExpect(MockMvcResultMatchers.status().isNotFound())  // HTTP 상태 코드가 404 Not Found인지 확인
                .andDo(MockMvcRestDocumentationWrapper.document("get-recipe-not-found",
                        ResourceSnippetParameters.builder()
                                .summary("존재하지 않는 레시피 조회")  // API 요약 정보
                                .description("존재하지 않는 ID로 레시피를 조회할 때의 오류 응답")  // API 상세 설명
                                .pathParameters(
                                        RequestDocumentation.parameterWithName("id").description("존재하지 않는 레시피 ID")
                                )
                                .build()
                ));
        // 이 테스트는 존재하지 않는 레시피 조회 시의 동작을 검증하고 문서화
    }
}