package me.longbow122.bot.listener;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import me.longbow122.bot.configuration.properties.DiscordConfigurationProperties;
import me.longbow122.datamodel.repository.entities.Copypasta;
import me.longbow122.bot.service.CopypastaService;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
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

	private final DiscordConfigurationProperties discordConfigurationProperties;


	@Override
	public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
		//Something bad must have happened for the guild to return null here.
		if (event.getGuild() == null) return;
		//* Standard Copypasta handling is here
		List<Copypasta> pastas = copypastaService.findAllCopypasta();
		if (pastas.stream().anyMatch(pasta -> pasta.getName().equals(event.getName()))) {
			Optional<Copypasta> found = copypastaService.findCopypastaByName(event.getName());
			if (found.isEmpty()) {
				event.reply("SOMETHING HAS GONE WRONG WITH COPYPASTA COMMANDS, THE FOUND COPYPASTA WAS EMPTY! PLEASE CONTACT AN ADMINISTRATOR!").setEphemeral(false).queue();
				return;
			}
			event.reply(found.get().getMessage()).setEphemeral(false).queue();
			return;
		}
		//* Worth noting that we are only checking cached members here, so need to ensure that members are cached properly when checking.
		Member user = event.getGuild().getMemberById(event.getUser().getIdLong());
		if (user == null) {
			event.reply("SOMETHING HAS GONE WRONG WITH COPYPASTA COMMANDS. WE COULD NOT FIND A USER!").setEphemeral(false).queue();
			return;
		}
		Role copypastaRole = event.getGuild().getRoleById(discordConfigurationProperties.copypastaRoleID());
		Role adminRole = event.getGuild().getRoleById(discordConfigurationProperties.adminRoleID());
		//* You need to have the copypasta role to be able to work with copypasta commands.
		if (!(user.getRoles().contains(copypastaRole))) {
			event.reply("You do not have permissions to use that command! You need " + copypastaRole.getName()).setEphemeral(true).queue();
			return;
		}
		//* The "admin" portion of the command handling. This is where copypasta adding, updating and removal is handled.
		//* Admin commands require the configured "admin" role.
		switch (event.getFullCommandName()) {
			//* Deal with the addition of copypastas here.
			case "copypasta add": {
				TextInput commandName = TextInput.create("name", "Command Name:", TextInputStyle.SHORT).setPlaceholder("Enter the name of the command.").setMinLength(1).setMaxLength(32).build();
				TextInput commandDescription = TextInput.create("description", "Description:", TextInputStyle.SHORT).setPlaceholder("Enter a description of the command.").setMinLength(1).setMaxLength(100).build();
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
					if (!(user.getRoles().contains(adminRole))) {
						event.reply("You do not have permission to use that command! You need " + adminRole.getName()).setEphemeral(true).queue();
						return;
					}
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
				if (!(user.getRoles().contains(adminRole))) {
					event.reply("You do not have permission to use that command! You need " + adminRole.getName()).setEphemeral(true).queue();
					return;
				}
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
							if (!validateName(valueEntered)) {
								event.reply("Looks like the name you entered was invalid. Please use a valid name when updating the copypasta **" + nameEntered + "**").setEphemeral(true).queue();
								return;
							}
							copypastaService.updateCopypastaName(nameEntered, valueEntered);
							break;
						}
						case "description": {
							if (!validateDescription(valueEntered)) {
								event.reply("Looks like the description you entered was invalid. Please use a valid description when updating the copypasta **" + nameEntered + "**").setEphemeral(true).queue();
								return;
							}
							copypastaService.updateCopypastaDescription(nameEntered, valueEntered);
							break;
						}
						case "message": {
							if (!validateMessage(valueEntered)) {
								event.reply("Looks like the message you entered was invalid. Please use a valid message when updating the copypasta **" + nameEntered + "**").setEphemeral(true).queue();
								return;
							}
							copypastaService.updateCopypastaMessage(nameEntered, valueEntered);
							break;
						}
						default: {
							event.reply("Looks like a field with that name does NOT exist. Try updating a field that exists.").setEphemeral(true).queue();
							return;
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
				event.reply("THE COPYPASTA YOU TRIED SENDING HAS EITHER BEEN DELETED OR DOES NOT EXIST. PLEASE CONTACT AN ADMIN IF YOU BELIEVE THAT THIS IS IN ERROR.").setEphemeral(false).queue();
			}
		}
	}

	private boolean validateName(String name) {
		if (name.isBlank() || name.length() > 32) return false;
		for (char i : name.toCharArray()) {
			if(!(Character.isLowerCase(i)) || !(Character.isAlphabetic(i)) || i == ' ') return false;
		}
		return true;
	}

	private boolean validateDescription(String description) {
		return !description.isBlank() && description.length() <= 100;
	}

	private boolean validateMessage(String message) {
		return !message.isBlank() && message.length() <= 2000;
	}
}
