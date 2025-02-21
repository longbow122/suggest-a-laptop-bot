package me.longbow122.bot.listener;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.longbow122.bot.configuration.DiscordConfigurer;
import me.longbow122.bot.configuration.properties.FormConfigurationProperties;
import me.longbow122.bot.dto.CopypastaDTO;
import me.longbow122.bot.exception.exceptions.ChannelNotFoundException;
import me.longbow122.bot.service.CopypastaService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class CopypastaModalListener extends ListenerAdapter {

	private final CopypastaService copypastaService;

	private final FormConfigurationProperties formConfigurationProperties;

	private final DiscordConfigurer discordConfigurer;

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
				return;
			} catch (IllegalArgumentException e) {
				event.getUser().openPrivateChannel().queue(channel -> channel.sendMessage(e.getMessage()).queue());
				return;
			} catch (EntityExistsException e) {
				String toSend = "Looks like a command with that name already exists. Try again. \n Name: **" + nameEntered + "**\n Message: **" + messageEntered + "**" + "\n Description: **" + descriptionEntered + "**";
				if (toSend.length() > 2000) {
					event.getUser().openPrivateChannel().queue(channel -> channel.sendMessage("Looks like a command with that name already exists. Try again. \n Name: **" + nameEntered + "**\n Description: **" + descriptionEntered + "**\n Message: \n").queue());
					event.getHook().getInteraction().getUser().openPrivateChannel().queue(channel -> channel.sendMessage(messageEntered).queue());
					return;
				}
				event.getUser().openPrivateChannel().queue(channel -> channel.sendMessage(toSend).queue());
				return;
			}
		}
		Set<String> formCategories = formConfigurationProperties.forms().keySet();
		if (formCategories.contains(event.getModalId())) {
			FormConfigurationProperties.Form form = formConfigurationProperties.forms().get(event.getModalId());
			TextChannel formChannel = discordConfigurer.getJda().getTextChannelById(form.formChannel());
			if (formChannel == null) {
				event.reply("Something went wrong in finding the right form channel. Please contact longbow122!").setEphemeral(false).queue();
				throw new ChannelNotFoundException("Form channel not found! Please check the right forms and see if the configuration is correct!");
			}
			List<String> potentialAnswers = new ArrayList<>();
			for (int i = 0; i < form.questions().size(); i++) {
				potentialAnswers.add(Objects.requireNonNull(event.getValue("question" + i)).getAsString());
			}
			formChannel.sendMessage(getFormattedForm(event.getUser(), form.questions(), potentialAnswers)).queue();
			event.reply("Your form has been sent to the relevant channel! Please wait for a response!").setEphemeral(true).queue();
		}
	}

	private String getFormattedForm(User user, List<String> questions, List<String> answers) {
		StringBuilder formattedForm = new StringBuilder();
		for (int index = 0; index < questions.size(); index++) {
			formattedForm.append("**").append(questions.get(index)).append("**");
			formattedForm.append("\n");
			formattedForm.append(answers.get(index)).append("\n");
		}
		formattedForm.append("**Posted by: **").append(user.getAsMention());
		return formattedForm.toString();
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
