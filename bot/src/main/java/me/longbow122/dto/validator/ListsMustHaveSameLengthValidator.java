package me.longbow122.dto.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;
import java.util.List;

public class ListsMustHaveSameLengthValidator implements ConstraintValidator<ListsMustHaveSameLength, Object> {

	private String firstList;
	private String secondList;

	@Override
	public void initialize(ListsMustHaveSameLength constraintAnnotation) {
		firstList = constraintAnnotation.firstList();
		secondList = constraintAnnotation.secondList();
	}

	@Override
	public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
		try {
			Field firstField = o.getClass().getDeclaredField(this.firstList);
			Field secondField = o.getClass().getDeclaredField(this.secondList);
			firstField.setAccessible(true);
			secondField.setAccessible(true);
			List<?> firstListObj = (List<?>) firstField.get(o);
			List<?> secondListObj = (List<?>) secondField.get(o);
			return firstListObj.size() == secondListObj.size();
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException("Error validating the length of lists!");
		}
	}
}
