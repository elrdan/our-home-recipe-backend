package com.ourhomerecipe.domain.tag;

import com.ourhomerecipe.dto.recipe.enums.Tagable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor @AllArgsConstructor
public class Tag {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tag_id")
	private Long id;

	private String name;

	@ManyToOne
	@JoinColumn(name = "tag_type_id")
	private TagType tagType;

	@Builder
	public Tag(Long id, Tagable name, TagType tagType) {
		this.id = id;
		this.name = name.getTagValue();
		this.tagType = tagType;
	}
}
