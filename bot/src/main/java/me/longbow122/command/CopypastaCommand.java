package me.longbow122.command;

import com.freya02.botcommands.api.annotations.CommandMarker;
import com.freya02.botcommands.api.application.ApplicationCommand;
import com.freya02.botcommands.api.application.CommandScope;
import com.freya02.botcommands.api.application.slash.GlobalSlashEvent;
import com.freya02.botcommands.api.application.slash.annotations.JDASlashCommand;
import com.freya02.botcommands.api.prefixed.annotations.TextOption;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import me.longbow122.service.CopypastaService;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.TransactionSystemException;

import java.util.Arrays;
import java.util.List;

@CommandMarker
@RequiredArgsConstructor
public class CopypastaCommand extends ApplicationCommand {

	private final CopypastaService copypastaService;

	@JDASlashCommand(name = "copypasta", scope = CommandScope.GLOBAL, subcommand = "add", description = "Add a Copypasta")
	public void copypastaAdd(GlobalSlashEvent event) {
		//* This handler will deal with the addition of Copypastas
		TextInput commandName = TextInput.create("name", "Command Name:", TextInputStyle.SHORT).setPlaceholder("Enter the name of the command.").setMinLength(1).setMaxLength(32).build();
		TextInput commandDescription = TextInput.create("description", "Description:", TextInputStyle.PARAGRAPH).setPlaceholder("Enter a description of the command.").setMinLength(1).setMaxLength(100).build();
		TextInput commandMessage = TextInput.create("message", "Message:", TextInputStyle.PARAGRAPH).setPlaceholder("Enter the message to be sent.").setMinLength(1).setMaxLength(2000).build();
		Modal modal = Modal.create("copypastaAdd", "Add a new copypasta")
			.addActionRow(commandName)
			.addActionRow(commandDescription)
			.addActionRow(commandMessage)
			.build();
		event.replyModal(modal).queue();
	}

	@JDASlashCommand(name = "copypasta", description = "Remove a copypasta", subcommand = "remove")
	public void copypastaRemove(GlobalSlashEvent event, @TextOption(name = "name") String copypastaName) {
		//* This handler will deal with the removal of Copypastas
		try {
			copypastaService.deleteCopypasta(copypastaName);
		} catch (EntityNotFoundException e) {
			event.reply("Looks like a command with that name does NOT exist. Try deleting a copypasta that exists.").setEphemeral(true).queue();
		}
	}

	@JDASlashCommand(name = "copypasta", description = "Update a copypasta", subcommand = "update")
	public void copypastaUpdate(GlobalSlashEvent event, @TextOption(name = "name") String copypastaName, @TextOption(name = "field") String fieldEntered, @TextOption(name = "value") String valueEntered) {
		//* This handler will deal with the updating of Copypastas
		List<String> fieldVals = Arrays.asList("name", "description", "message");
		if(!(fieldVals.contains(fieldEntered))) {
			event.reply("Looks like a field with that name does NOT exist. Try updating a field that exists.").setEphemeral(true).queue();
			return;
		}
		try {
			switch (fieldEntered) {
				case "name": {
					copypastaService.updateCopypastaName(copypastaName, valueEntered);
					break;
				}
				case "description": {
					copypastaService.updateCopypastaDescription(copypastaName, valueEntered);
					break;
				}
				case "message": {
					copypastaService.updateCopypastaMessage(copypastaName, valueEntered);
					break;
				}
			}
			event.reply("Copypasta successfully updated! \n Name: **" + copypastaName + "** \n Field: **" + fieldEntered + "** \n Value: **" + valueEntered + "**").setEphemeral(true).queue();
		} catch (EntityNotFoundException e) {
			event.reply("Looks like a command with that name does NOT exist. Try updating a copypasta that exists.").setEphemeral(true).queue();
		} catch (DataIntegrityViolationException | TransactionSystemException e) {
			event.reply("The field you tried updating did not adhere to the constraints of Copypastas! \n Names must be between 1-32 characters, with no numbers and no whitespaces and **unique**. \n Descriptions must be between 1-100 characters. \n Messages must be between 1-2000 characters.").setEphemeral(true).queue();
		}
	}
}
