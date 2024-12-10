package me.longbow122.bot.dto.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ListsMustHaveSameLengthValidator.class)
@Documented
public @interface ListsMustHaveSameLength {
	String message() default "Questions and Answers must be of the same length!";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	String firstList();

	String secondList();
}
