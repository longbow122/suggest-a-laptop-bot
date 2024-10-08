package me.longbow122.dto.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LowercaseOnlyValidator implements ConstraintValidator<LowercaseOnly, String> {

	@Override
	public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
		if (s == null) return false;
		for (char i : s.toCharArray()) {
			if(!(Character.isLowerCase(i)) || !(Character.isAlphabetic(i)) || i == ' ') return false;
		}
		return true;
	}


}
