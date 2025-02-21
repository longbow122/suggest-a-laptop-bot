package me.longbow122.bot.configuration.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import me.longbow122.bot.dto.validator.ListsMustHaveSameLength;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "form")
public record FormConfigurationProperties(
	@Size(max = 25)
	Map<String, Form> forms
) {

	//TODO ALSO NEED TO TEST WHETHER THESE FORMS ARE PROPERLY CONFIGURABLE! TEST THAT WE CAN CHANGE QUESTIONS AND PLACEHOLDERS AND CHANNELS ON THE FLY
	// WITH THE CHANGE OF A CONFIGURATION VALUE!

	//TODO, TEST THAT WE CAN ONLY HAVE QUESTIONS OF THE RIGHT LENGTH INSIDE THE LIST!
	//TODO TEST THAT WE CAN ONLY HAVE PLACEHOLDERS OF THE RIGHT LENGTH INSIDE THE LIST!
	//TODO TEST THAT BOTH LISTS MUST BE OF THE SAME SIZE WHEN BEING ADDED TO THE CONFIG!

	//TODO SHOULD WE BE WRITING UNIT TESTS FOR THIS CLASS TO TEST THE RIGHT VALIDATIONS ON THEM?

	//TODO WE CURRENTLY HAVE A SETTER FOR THIS CLASS, BUT WE SHOULD AVOID IT IF WE DON'T NEED IT!
	// SPRING CONFIGURATION BINDING SEEMS TO NEED A PLAIN CONSTRUCTOR TO WORK, AND THEN SETS THE VALUES ACCORDINGLY. CAN WE POTENTIALLY FIND A WAY TO HANDLE THIS PROPERLY?
	// WE NEED TO AVOID THE USE OF A SETTER IF WE CAN, SINCE WE DO NOT WANT IT THERE, AND WE DO NOT WANT TO HAVE A SETTER WHERE IT IS NOT USED IN THE CODE!
	@ListsMustHaveSameLength(firstList = "questions", secondList = "placeholders", message = "Questions and Placeholders must be of the same length! Please check your configuration!")
	@Getter
	@Setter
	public static class Form {
		@NotNull(message = "Channel ID cannot be null! Please check your configuration!")
		@NotBlank(message = "Channel ID cannot be blank! Please check your configuration!")
		private long formChannel;

		@NotNull
		@NotEmpty(message = "Questions cannot be empty! Please check your configuration!")
		@Size(max = 5)
		private List<@NotNull @NotEmpty @Size(min = 1, max = 45) String> questions;

		@NotNull
		@Size(max = 5)
		@NotEmpty(message = "Placeholders cannot be empty! Please check your configuration!")
		private List<@NotNull @NotEmpty @Size(min = 1, max = 100) String> placeholders;

		//TODO BELOW CONSTRUCTOR IS NOT BEING USED BY SPRING, LOOK TO REMOVE IT?
		public Form(long formChannel, List<String> questions, List<String> placeholders) {
			this.formChannel = formChannel;
			this.questions = questions;
			this.placeholders = placeholders;
		}

		//TODO NEED TO FIND A WAY TO AVOID USING A SETTER TO ALLOW SPRING TO MAP THINGS, SO WE CAN ADD THE RIGHT OBJECTS WITH A CONSTRUCTOR INSTEAD?
		public Form() {
		}
	}
}
