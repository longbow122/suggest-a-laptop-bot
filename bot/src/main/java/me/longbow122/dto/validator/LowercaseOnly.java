package me.longbow122.dto.validator;

import jakarta.validation.Constraint;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LowercaseOnlyValidator.class)
@Documented
public @interface LowercaseOnly {
	String message() default "Must contain only lowercase characters (no spaces)!";
}
