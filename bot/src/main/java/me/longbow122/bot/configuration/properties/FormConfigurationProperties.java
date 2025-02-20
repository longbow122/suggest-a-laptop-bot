package me.longbow122.bot.configuration.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import me.longbow122.bot.dto.validator.ListsMustHaveSameLength;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "form")
public record FormConfigurationProperties(
	@Size(max = 25)
	Map<String, Form> forms
) {

	//TODO SHOULD WE MOVE THIS FORM CLASS OUT AND PUT SOME METHODS IN IT TO MAKE OUR LIVES EASIER?
	// SEND FORM METHOD WOULD GO WELL IN HERE!
	@ListsMustHaveSameLength(firstList = "questions", secondList = "placeholders", message = "Questions and Placeholders must be of the same length! Please check your configuration!")
	@Getter
	public static class Form {
		@NotNull(message = "Channel ID cannot be null! Please check your configuration!")
		@NotBlank(message = "Channel ID cannot be blank! Please check your configuration!")
		private long formChannel;

		@NotNull
		@NotEmpty(message = "Questions cannot be empty! Please check your configuration!")
		@Size(max = 5)
		private List<@NotNull @NotEmpty @Size(min = 1, max = 45) String> questions;

		//! This field here does not have any validation on its inner elements, since validating them properly is complicated. We need to validate it according to the size of the field it's used in.
		//! Could this be a problem? Do we need to validate it?
		@NotNull
		@Size(max = 5)
		@NotEmpty(message = "Placeholders cannot be empty! Please check your configuration!")
		private List<String> placeholders;

		public Form(long formChannel, List<String> questions, List<String> placeholders) {
			this.formChannel = formChannel;
			this.questions = questions;
			this.placeholders = placeholders;
		}
	}
}
