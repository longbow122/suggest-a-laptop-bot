package me.longbow122.listener;

import lombok.RequiredArgsConstructor;
import me.longbow122.dto.CopypastaDTO;
import me.longbow122.service.CopypastaService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Objects;

@RequiredArgsConstructor
public class CopypastaModalListener extends ListenerAdapter {

	private final CopypastaService copypastaService;

	@Override
	public void onModalInteraction(@NotNull ModalInteractionEvent event) {
		//* Handles the addition of Copypastas
		if (event.getModalId().equals("copypastaAdd")) {
			String nameEntered = Objects.requireNonNull(event.getValue("name")).getAsString();
			String descriptionEntered = Objects.requireNonNull(event.getValue("description")).getAsString();
			String messageEntered = Objects.requireNonNull(event.getValue("message")).getAsString();
			try {
				CopypastaDTO added = new CopypastaDTO(nameEntered, descriptionEntered, messageEntered);
				copypastaService.createCopypasta(added);
				event.reply(MessageCreateData.fromEmbeds(getCommandAddedEmbed(nameEntered, descriptionEntered, messageEntered))).queue();
			} catch (IllegalArgumentException e) {
				event.reply(e.getMessage()).queue();
			} catch (DataIntegrityViolationException e) {
				event.reply("Looks like a command with that name already exists. Try again. \n Message: **" + messageEntered + "**" + "\n Description: **" + descriptionEntered + "**")
					.setEphemeral(true)
					.queue();
			}
		}
	}

	private MessageEmbed getCommandAddedEmbed(String name, String description, String message) {
		EmbedBuilder b = new EmbedBuilder();
		b.setAuthor("Command Added!");
		b.addField("Name", name, true);
		b.addField("Description", description, true);
		b.addField("Message", message, false);
		b.addField("Restart me!", "Due to current limitations, it is advised that you restart me after adding/removing a set of commands. Please restart me :(", false);
		b.setFooter("Contact longbow122 if there are issues with this bot.");
		return b.build();
	}
}
