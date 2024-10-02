package com.ourhomerecipe.dto.recipe.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = TagValidator.class)
@Target({ ElementType.FIELD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTag {
	String message() default "태그 타입에 정의되어있지 않은 태그입니다.";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
