package me.longbow122.bot.listener;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.longbow122.bot.dto.CopypastaDTO;
import me.longbow122.bot.dto.FormDTO;
import me.longbow122.bot.service.CopypastaService;
import me.longbow122.bot.service.FormService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class CopypastaModalListener extends ListenerAdapter {

	private final CopypastaService copypastaService;

	private final FormService formService;

	/*
	* We do not get exceptions or much control over invalid input at the Modal layer, so a lot of this validation does not end up triggering.
	* Good to have either way, in my opinion.
	* Our definition of the modal will ensure that we validate as much input where possible, making sure valid input is sent through.
	 */

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
				if (messageEntered.length() > 1024) {
					event.reply("Copypasta added successfully! \n **Name:** " + nameEntered + " \n **Description:** " + descriptionEntered + "\n **Message:** \n").queue();
					event.getHook().sendMessage(messageEntered).queue();
					return;
				}
				event.reply(MessageCreateData.fromEmbeds(getCommandAddedEmbed(nameEntered, descriptionEntered, messageEntered))).queue();
			} catch (IllegalArgumentException e) {
				event.getUser().openPrivateChannel().queue(channel -> channel.sendMessage(e.getMessage()).queue());
			} catch (EntityExistsException e) {
				String toSend = "Looks like a command with that name already exists. Try again. \n Name: **" + nameEntered + "**\n Message: **" + messageEntered + "**" + "\n Description: **" + descriptionEntered + "**";
				if (toSend.length() > 2000) {
					event.getUser().openPrivateChannel().queue(channel -> channel.sendMessage("Looks like a command with that name already exists. Try again. \n Name: **" + nameEntered + "**\n Description: **" + descriptionEntered + "**\n Message: \n").queue());
					event.getHook().getInteraction().getUser().openPrivateChannel().queue(channel -> channel.sendMessage(messageEntered).queue());
					return;
				}
				event.getUser().openPrivateChannel().queue(channel -> channel.sendMessage(toSend).queue());
			}
		}
		if (event.getModalId().equals("formSend")) {
			FormDTO form = new FormDTO(event.getUser().getName(), 1L, List.of(
				"What currency will you be purchasing in? What is your budget?",
				"How would you prioritise form factor, build quality, performance and battery life? Do you have a preferred screen size?",
				"Are you doing any CAD/Video Editing/Gaming? List which programs/games you would like to run.",
				"Any specific requirements such as good keyboard, touch-screen, 2-in-1, fingerprint reader, optical drive, etc?",
				"Leave any finishing thoughts here you feel are potentially necessary and beneficial to the discussion."), List.of(
				Objects.requireNonNull(event.getValue("question1")).getAsString(),
				Objects.requireNonNull(event.getValue("question2")).getAsString(),
				Objects.requireNonNull(event.getValue("question3")).getAsString(),
				Objects.requireNonNull(event.getValue("question4")).getAsString(),
				Objects.requireNonNull(event.getValue("question5")).getAsString()));
			formService.postForm(form);
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
