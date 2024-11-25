package me.longbow122.bot.listener;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class SlashFormCommandListener extends ListenerAdapter {

	//TODO IF THIS WORKS, THE ENTIRE API MODULE NEEDS TO BE REMOVED!

	@Override
	public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
		if (event.getGuild() == null) return;
		//* Handle listening for the form command through this command interaction.
		//TODO IF THIS IS THE ONLY CASE WE END UP HAVING, REDUCE TO BE AN IF-STATEMENT.
		switch (event.getFullCommandName()) {
			case "form": {
				//TODO IMPLEMENT MODAL HERE FOR FORMS!
				// THEN WE NEED TO TAKE QUESTIONS AND THE ANSWERS AND PUT THEM IN!
				// NEED TO USE FORM SERVICE TO HANDLE THE POSTING SINCE THAT WILL HAVE EVERYTHING WE NEED
				// CAN HARDCODE THE FORMDTO
				TextInput questionOne = TextInput.create("question1", "What currency will you be purchasing in? What is your budget?", TextInputStyle.PARAGRAPH).setMinLength(3).setMaxLength(300).build();
				TextInput questionTwo = TextInput.create("question2", "How would you prioritise form factor, build quality, performance and battery life? Do you have a preferred screen size?", TextInputStyle.PARAGRAPH).setMinLength(50).setMaxLength(300).build();
				TextInput questionThree = TextInput.create("question3", "Are you doing any CAD/Video Editing/Gaming? List which programs/games you would like to run.", TextInputStyle.PARAGRAPH).setMinLength(1).setMaxLength(300).build();
				TextInput questionFour = TextInput.create("question4", "Any specific requirements such as good keyboard, touch-screen, 2-in-1, fingerprint reader, optical drive, etc?", TextInputStyle.PARAGRAPH).setMinLength(1).setMaxLength(300).build();
				TextInput questionFive = TextInput.create("question5", "Leave any finishing thoughts here you feel are potentially necessary and beneficial to the discussion.", TextInputStyle.PARAGRAPH).setMinLength(1).setMaxLength(300).build();
				Modal modal = Modal.create("formSend", "Get a laptop reccomendation")
					.addActionRow(questionOne)
					.addActionRow(questionTwo)
					.addActionRow(questionThree)
					.addActionRow(questionFour)
					.addActionRow(questionFive)
					.build();
				event.replyModal(modal).queue();
			}
		}
	}
}
