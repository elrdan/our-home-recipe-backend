package com.ourhomerecipe.domain.ingredient;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor @AllArgsConstructor
public class UnitType {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "unit_type_id")
	private long id;

	@Enumerated(EnumType.STRING)
	private com.ourhomerecipe.domain.ingredient.enums.UnitType name;
}
