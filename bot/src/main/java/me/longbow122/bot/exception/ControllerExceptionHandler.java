package me.longbow122.bot.exception;

import me.longbow122.bot.exception.exceptions.ChannelNotFoundException;
import me.longbow122.bot.exception.exceptions.GuildNotFoundException;
import me.longbow122.bot.exception.exceptions.UserNotFoundException;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice(basePackages = {
	"me.longbow122.api.controller"
})
@ComponentScan(basePackages = {
	"me.longbow122.bot.service",
	"me.longbow122.api.controller"
})
public class ControllerExceptionHandler {

	@ExceptionHandler(ChannelNotFoundException.class)
	public ResponseEntity<String> handleChannelNotFoundException(ChannelNotFoundException exception) {
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException exception) {
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(GuildNotFoundException.class)
	public ResponseEntity<String> handleGuildNotFoundException(GuildNotFoundException exception) {
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<List<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
		//* Get all the violations that might have been caught against the API, so we can check against one of them in testing.
		List<String> errorMapping = new ArrayList<>();
		exception.getBindingResult().getAllErrors().forEach(error ->
			errorMapping.add(error.getDefaultMessage()));
		return new ResponseEntity<>(errorMapping, HttpStatus.BAD_REQUEST);
	}
}
