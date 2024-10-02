package com.ourhomerecipe.dto.recipe.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngredientReqDto {
	@NotNull(message = "재료 식별자는 필수입니다.")
	private Long id;

	@NotBlank(message = "재료 이름은 필수입니다.")
	private String name;

	@NotBlank(message = "재료 수량은 필수입니다.")
	private String quantity;
}
