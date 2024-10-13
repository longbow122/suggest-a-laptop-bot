package me.longbow122.bot.exception.exceptions;

import jakarta.persistence.EntityNotFoundException;

public class ChannelNotFoundException extends EntityNotFoundException {
	public ChannelNotFoundException(String message) {
		super(message);
	}
}
