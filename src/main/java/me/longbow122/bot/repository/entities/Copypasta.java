package me.longbow122.bot.repository.entities;

import lombok.Getter;


@Getter
public class Copypasta {

	private static final int NAME_MAX_LENGTH = 32;

	private static final int DESCRIPTION_MAX_LENGTH = 100;

	private static final int MESSAGE_MAX_LENGTH = 2000;

	private String name;

	private String description;

	private String message;

	public Copypasta(String name, String description, String message) {
		setName(name);
		setDescription(description);
		setMessage(message);
	}

	public void validate() {
		validateName(name);
		validateRequiredField("Description", description, DESCRIPTION_MAX_LENGTH);
		validateRequiredField("Message", message, MESSAGE_MAX_LENGTH);
	}

	public void setName(String name) {
		validateName(name);
		this.name = name;
	}

	public void setDescription(String description) {
		validateRequiredField("Description", description, DESCRIPTION_MAX_LENGTH);
		this.description = description;
	}

	public void setMessage(String message) {
		validateRequiredField("Message", message, MESSAGE_MAX_LENGTH);
		this.message = message;
	}

	private void validateName(String name) {
		validateRequiredField("Name", name, NAME_MAX_LENGTH);
		for (char i : name.toCharArray()) {
			if (!(Character.isLowerCase(i)) || !(Character.isAlphabetic(i)) || i == ' ') {
				throw new IllegalArgumentException("Names must contain only lowercase characters (no spaces)!");
			}
		}
	}

	private void validateRequiredField(String fieldName, String value, int maxLength) {
		if (value == null) throw new IllegalArgumentException(fieldName + " cannot be null");
		if (value.isBlank()) throw new IllegalArgumentException(fieldName + " cannot be blank");
		if (value.length() > maxLength) {
			throw new IllegalArgumentException(fieldName + " must be between 1 and " + maxLength + " characters");
		}
	}
}
