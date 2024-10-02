package com.ourhomerecipe.dto.common.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<ValidEnum, String> {
	private Class<? extends Enum<?>> enumClass;

	@Override
	public void initialize(ValidEnum constraintAnnotation) {
		enumClass = constraintAnnotation.enumClass();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}

		boolean result = false;
		for (Enum<?> enumVal : enumClass.getEnumConstants()) {
			if (enumVal.name().equals(value)) {
				result = true;
				break;
			}
		}
		return result;
	}
}
