package me.longbow122.bot.exception.exceptions;

import jakarta.persistence.EntityNotFoundException;

public class GuildNotFoundException extends EntityNotFoundException {
	public GuildNotFoundException(String message) {
		super(message);
	}
}
