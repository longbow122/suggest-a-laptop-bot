package me.longbow122.bot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import me.longbow122.bot.dto.validator.ListsMustHaveSameLength;

import java.util.List;

@ListsMustHaveSameLength(firstList = "questions", secondList = "answers")
public record FormDTO(
	@Getter
	@NotNull(message = "Username cannot be null!")
	@NotBlank(message = "Username cannot be blank!")
	String poster,
	@Getter
	@NotNull(message = "Channel ID cannot be null!")
	long channelSendID,
	@Getter
	@NotNull(message = "Questions cannot be null!")
	@NotEmpty(message = "Questions cannot be empty!")
	List<String> questions,
	@Getter
	@NotNull(message = "Answers cannot be null!")
	@NotEmpty(message = "Answers cannot be empty!")
	List<String> answers
) {
}
