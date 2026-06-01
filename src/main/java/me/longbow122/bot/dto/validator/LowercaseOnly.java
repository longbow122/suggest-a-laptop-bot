package me.longbow122.bot.dto.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LowercaseOnlyValidator.class)
@Documented
public @interface LowercaseOnly {
	String message() default "Names must contain only lowercase characters (no spaces)!";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
