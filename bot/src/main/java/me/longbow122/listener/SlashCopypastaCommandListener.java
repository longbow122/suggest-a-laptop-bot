package me.longbow122.listener;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import me.longbow122.datamodel.repository.entities.Copypasta;
import me.longbow122.service.CopypastaService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.TransactionSystemException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class SlashCopypastaCommandListener extends ListenerAdapter {

	private final CopypastaService copypastaService;


	@Override
	public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
		//Something bad must have happened for the guild to return null here.
		if (event.getGuild() == null) return;
		//* Standard Copypasta handling is here
		List<Copypasta> pastas = copypastaService.findAllCopypasta();
		if (pastas.stream().anyMatch(pasta -> pasta.getName().equals(event.getName()))) {
			Optional<Copypasta> found = copypastaService.findCopypastaByName(event.getName());
			if (found.isEmpty()) {
				event.reply("SOMETHING HAS GONE WRONG WITH COPYPASTA COMMANDS, PLEASE CONTACT AN ADMINISTRATOR!").setEphemeral(false).queue();
				return;
			}
			event.reply(copypastaService.findCopypastaByName(event.getName()).get().getMessage()).setEphemeral(false).queue();
			return;
		}

		//* The "admin" portion of the command handling. This is where copypasta adding, updating and removal is handled.
		switch (event.getFullCommandName()) {
			//* Deal with the addition of copypastas here.
			case "copypasta add": {
				TextInput commandName = TextInput.create("name", "Command Name:", TextInputStyle.SHORT).setPlaceholder("Enter the name of the command.").setMinLength(1).setMaxLength(32).build();
				TextInput commandDescription = TextInput.create("description", "Description:", TextInputStyle.PARAGRAPH).setPlaceholder("Enter a description of the command.").setMinLength(1).setMaxLength(100).build();
				TextInput commandMessage = TextInput.create("message", "Message:", TextInputStyle.PARAGRAPH).setPlaceholder("Enter the message to be sent.").setMinLength(1).setMaxLength(2000).build();
				Modal modal = Modal.create("copypastaAdd", "Add a new copypasta")
					.addActionRow(commandName)
					.addActionRow(commandDescription)
					.addActionRow(commandMessage)
					.build();
				event.replyModal(modal).queue();
				return;
			}
			//* Deal with the removal of copypastas here.
			case "copypasta remove": {
				try {
					copypastaService.deleteCopypasta(event.getOption("name").getAsString());
					event.reply("Successfully deleted copypasta with name: " + event.getOption("name").getAsString()).setEphemeral(false).queue();
					return;
				} catch (EntityNotFoundException e) {
					event.reply("Looks like a command with that name does NOT exist. Try deleting a copypasta that exists.").setEphemeral(true).queue();
					return;
				}
			}
			//* Deal with the updating of copypastas here.
			case "copypasta update": {
				String nameEntered = event.getOption("name").getAsString();
				String fieldEntered = event.getOption("field").getAsString();
				String valueEntered = event.getOption("value").getAsString();
				List<String> fieldVals = Arrays.asList("name", "description", "message");
				if (!(fieldVals.contains(fieldEntered))) {
					event.reply("Looks like a field with that name does NOT exist. Try updating a field that exists.").setEphemeral(true).queue();
					return;
				}
				try {
					switch (fieldEntered) {
						case "name": {
							copypastaService.updateCopypastaName(nameEntered, valueEntered);
							break;
						}
						case "description": {
							copypastaService.updateCopypastaDescription(nameEntered, valueEntered);
							break;
						}
						case "message": {
							copypastaService.updateCopypastaMessage(nameEntered, valueEntered);
							break;
						}
					}
					event.reply("Copypasta successfully updated! \n Name: **" + nameEntered + "** \n Field: **" + fieldEntered + "** \n Value: **" + valueEntered + "**").setEphemeral(true).queue();
				} catch (EntityNotFoundException e) {
					event.reply("Looks like a command with that name does NOT exist. Try updating a copypasta that exists.").setEphemeral(true).queue();
				} catch (DataIntegrityViolationException | TransactionSystemException e) {
					event.reply("The field you tried updating did not adhere to the constraints of Copypastas! \n Names must be between 1-32 lowercase characters, with no numbers and no whitespaces and **unique**. \n Descriptions must be between 1-100 characters. \n Messages must be between 1-2000 characters.").setEphemeral(true).queue();
				}
				return;
			}
			default: {
				event.reply("SOMETHING HAS GONE WRONG WITH THE COPYPASTA COMMANDS. PLEASE CONTACT AN ADMIN ASAP.").setEphemeral(false).queue();
			}
		}
	}
}
