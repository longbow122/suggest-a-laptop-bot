package me.longbow122.listener;

import lombok.RequiredArgsConstructor;
import me.longbow122.datamodel.repository.entities.Copypasta;
import me.longbow122.service.CopypastaService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class SlashCopypastaCommandListener extends ListenerAdapter {

	private final CopypastaService copypastaService;


	@Override
	public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
		//Something bad must have happened for the guild to return null here.
		if (event.getGuild() == null) return;
		//* Standard copypasta handling is done here.
		List<Copypasta> pastas = copypastaService.findAllCopypasta();
		if (pastas.stream().anyMatch(pasta -> pasta.getName().equals(event.getName()))) {
			Optional<Copypasta> found = copypastaService.findCopypastaByName(event.getName());
			if (found.isEmpty()) {
				event.reply("The copypasta you tried to enter no longer exists! You entered: " + event.getName()).setEphemeral(false).queue();
				return;
			}
			event.reply(found.get().getMessage()).setEphemeral(false).queue();
		}
	}
}
