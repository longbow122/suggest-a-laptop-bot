package me.longbow122.bot.listener;

import lombok.RequiredArgsConstructor;
import me.longbow122.bot.service.CopypastaService;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
public class CopypastaAutocompleteListener extends ListenerAdapter {

	private final List<Command.Choice> fieldChoices = Arrays.asList(new Command.Choice("name", "name"), new Command.Choice("description", "description"), new Command.Choice("message", "message"));

	private final CopypastaService copypastaService;

	@Override
	public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
		if (event.getFullCommandName().equals("copypasta update")) {
			switch (event.getFocusedOption().getName()) {
				case "name": {
					List<Command.Choice> foundChoices = new ArrayList<>();
					copypastaService.findAllCopypastaStartsWith(event.getOption("name").getAsString()).forEach(c ->
						foundChoices.add(new Command.Choice(c.getName(), c.getName())));
					event.replyChoices(foundChoices).queue();
					return;
				}
				case "field": {
					event.replyChoices(fieldChoices).queue();
				}
			}
		}
		if (event.getFullCommandName().equals("copypasta remove") && event.getFocusedOption().getName().equals("name")) {
			List<Command.Choice> foundChoices = new ArrayList<>();
			copypastaService.findAllCopypastaStartsWith(event.getOption("name").getAsString()).forEach(c -> {
				foundChoices.add(new Command.Choice(c.getName(), c.getName()));
			});
			event.replyChoices(foundChoices).queue();
		}
	}
}
