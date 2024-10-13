package me.longbow122.exception.exceptions;

import jakarta.persistence.EntityNotFoundException;

public class UserNotFoundException extends EntityNotFoundException {
	public UserNotFoundException(String message) {
		super(message);
	}
}
