package me.longbow122.bot.exception.exceptions;

import java.util.NoSuchElementException;

public class UserNotFoundException extends NoSuchElementException {
	public UserNotFoundException(String message) {
		super(message);
	}
}
