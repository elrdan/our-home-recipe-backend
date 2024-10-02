package com.ourhomerecipe.dto.recipe.annotation;

import java.lang.reflect.Constructor;

import com.ourhomerecipe.dto.recipe.enums.Tagable;
import com.ourhomerecipe.dto.recipe.request.TagReqDto;
import com.ourhomerecipe.dto.recipe.utils.TagTypeUtil;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TagValidator implements ConstraintValidator<ValidTag, TagReqDto> {

	@Override
	public void initialize(ValidTag constraintAnnotation) {
	}

	@Override
	public boolean isValid(TagReqDto tagReqDto, ConstraintValidatorContext context) {
		if (tagReqDto.getName() == null || tagReqDto.getType() == null) {
			return false;
		}

		// 태그 유형에 따라 해당하는 Tagable 클래스 설정
		Class<? extends Tagable> tagableClass = TagTypeUtil.getTagableClassForTagType(tagReqDto.getType());

		// 설정한 클래스가 Enum 클래스인지 확인
		if (Enum.class.isAssignableFrom(tagableClass)) {
			// 태그 이름과 일치하는지 체크
			for (Tagable tagable : tagableClass.getEnumConstants()) {
				if (tagable.getTagValue().equals(tagReqDto.getName())) {
					return true;
				}
			}
		} else {
			try {
				// Integer 파라미터를 필요로 하는 생성자를 찾아 인스턴스화
				Constructor<? extends Tagable> constructor = tagableClass.getDeclaredConstructor(Integer.class);
				Integer minutes = Integer.parseInt(tagReqDto.getName());
				Tagable tagable = constructor.newInstance(minutes);
				return tagable.isValid(tagReqDto.getName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}
