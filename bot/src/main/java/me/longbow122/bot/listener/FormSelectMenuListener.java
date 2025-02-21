package me.longbow122.bot.listener;

import lombok.RequiredArgsConstructor;
import me.longbow122.bot.configuration.properties.FormConfigurationProperties;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class FormSelectMenuListener extends ListenerAdapter {

	private final FormConfigurationProperties formConfigurationProperties;

	@Override
	public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
		if (event.getComponentId().equals("form-select")) {
			String selectedOption = event.getInteraction().getValues().getFirst();
			List<ItemComponent> disabledComponents = List.of(event.getComponent().asDisabled());
			event.getMessage().editMessage(event.getMessage().getContentRaw()).setActionRow(disabledComponents).queue();
			FormConfigurationProperties.Form foundForm = formConfigurationProperties.forms().get(selectedOption);
			event.replyModal(getFormModal(selectedOption, foundForm)).queue();
		}
	}

	private Modal getFormModal(String formCategory, FormConfigurationProperties.Form form) {
		int[] questionMinLengths = {3, 1, 1, 1, 1};
		int[] questionMaxLengths = {100, 100, 200, 300, 1000};
		List<TextInput> inputs = new ArrayList<>();
		List<String> questions = form.getQuestions();
		List<String> placeholders = form.getPlaceholders();
		for (int i = 0; i < questions.size(); i++) {
			inputs.add(TextInput.create("question" + i, questions.get(i), TextInputStyle.PARAGRAPH)
				.setPlaceholder(placeholders.get(i))
				.setMinLength(questionMinLengths[i]).setMaxLength(questionMaxLengths[i]).build());
		}
		Modal.Builder modal = Modal.create(formCategory, "Get a laptop recommendation");
		inputs.forEach(modal::addActionRow);
		return modal.build();
	}
}
