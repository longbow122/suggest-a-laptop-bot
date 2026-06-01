package me.longbow122.bot.exception.exceptions;

import java.util.NoSuchElementException;

public class GuildNotFoundException extends NoSuchElementException {
	public GuildNotFoundException(String message) {
		super(message);
	}
}
