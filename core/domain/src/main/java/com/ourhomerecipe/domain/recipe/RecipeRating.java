package com.ourhomerecipe.domain.recipe;

import java.math.BigDecimal;

import com.ourhomerecipe.domain.MutableBaseEntity;
import com.ourhomerecipe.domain.member.Member;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor @AllArgsConstructor
public class RecipeRating extends MutableBaseEntity {
	@EmbeddedId
	private RecipeRatingId id;

	// 정밀도, 오차없는 연산을 확보하기 위해 BigDecimal 사용
	@Column(precision = 2, scale = 1)
	private BigDecimal rating;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("recipeId")
	@JoinColumn(name = "recipe_id")
	private Recipe recipe;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("memberId")
	@JoinColumn(name = "member_id")
	private Member member;
}
