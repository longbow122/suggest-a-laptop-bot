package me.longbow122.bot.exception.exceptions;

import java.util.NoSuchElementException;

public class ChannelNotFoundException extends NoSuchElementException {
	public ChannelNotFoundException(String message) {
		super(message);
	}
}
