package com.ourhomerecipe.dto.recipe.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder @NoArgsConstructor @AllArgsConstructor
public class RecipeCommentResDto {
	// 코멘트 고유 번호
	private Long commentId;

	// 코멘트 내용
	private String comment;

	// 코멘트 작성자
	private String createdBy;

	// 코멘트 등록 시간
	private LocalDateTime createdAt;

	// 코멘트 수정 시간
	private LocalDateTime updatedAt;
}
