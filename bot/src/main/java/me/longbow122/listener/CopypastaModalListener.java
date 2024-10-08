package me.longbow122.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RequiredArgsConstructor
public class CopypastaModalListener extends ListenerAdapter {

	private final CopypastaService copypastaService;

	//TODO UNSURE WHY, BUT WHENEVER WE PASS INVALID INPUT, WE DO NOT OFTEN GET AN EXCEPTION....
	// UNSURE IF IT IS SOMETHING TO DO WITH OUR MODAL DEFINITION WITHIN THE COMMAND, BUT WE DO NOT GET AN EXCEPTION AND IT IS NOT LET THROUGH... STRANGE.
	// IT'S GOOD, BUT STILL STRANGE. PROBABLY ALSO PART OF THE REASON WE CANNOT CLOSE THE MODAL WHEN WE FAIL TO GIVE A CERTAIN TYPE OF VALID INPUT.

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
				event.getUser().openPrivateChannel().queue(channel -> channel.sendMessage(e.getMessage()).queue());
			} catch (DataIntegrityViolationException e) {
				event.getUser().openPrivateChannel().queue(channel -> channel.sendMessage("Looks like a command with that name already exists. Try again. \n Message: **" + messageEntered + "**" + "\n Description: **" + descriptionEntered + "**").queue());
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
