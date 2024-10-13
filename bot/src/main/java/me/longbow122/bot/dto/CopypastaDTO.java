package me.longbow122.bot.dto;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import me.longbow122.bot.dto.validator.LowercaseOnly;

import java.util.Set;

public record CopypastaDTO(

	@NotBlank(message = "Name cannot be blank")
	@NotNull(message = "Name cannot be null")
	@Size(min = 1, max = 32, message = "Name must be between 1 and 32 characters")
	@LowercaseOnly
	String name,
	@NotBlank(message = "Description cannot be blank")
	@NotNull(message = "Description cannot be null")
	@Size(min = 1, max = 100, message = "Description must be between 1 and 100 characters")
	String description,
	@NotBlank(message = "Message cannot be blank")
	@NotNull(message = "Message cannot be null")
	@Size(min = 1, max = 2000, message = "Message must be between 1 and 2000 characters")
	String message) {

	public CopypastaDTO(String name, String description, String message) {
		this.name = name;
		this.description = description;
		this.message = message;
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		Set<ConstraintViolation<CopypastaDTO>> violations = validator.validate(this);
		if (!(violations.isEmpty())) {
			throw new IllegalArgumentException(violations.iterator().next().getMessage());
		}
	}
}
