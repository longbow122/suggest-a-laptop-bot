package me.longbow122.service;

import me.longbow122.configuration.DiscordConfigurer;
import me.longbow122.configuration.properties.FormConfigurationProperties;
import me.longbow122.dto.FormDTO;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ConfigurationPropertiesScan(basePackages = {
	"me.longbow122.configuration"
})
@ComponentScan(basePackages = {
	"me.longbow122.configuration"
})
public class FormService {

	private final FormConfigurationProperties formConfigurationProperties;

	private final DiscordConfigurer discordConfigurer;

	@Autowired
	public FormService(FormConfigurationProperties formConfigurationProperties, DiscordConfigurer discordConfigurer) {
		this.formConfigurationProperties = formConfigurationProperties;
		this.discordConfigurer = discordConfigurer;
	}

	public void postForm(FormDTO form) {
		TextChannel formChannel = discordConfigurer.getJda().getTextChannelById(formConfigurationProperties.formChannelId());
		//TODO NEED TO FIND A WAY TO EASILY CATCH THESE EXCEPTIONS WITHIN THE CONTROLLER SO THAT WE CAN HANDLE THE INFORMATION PROPERLY.
		if (formChannel == null) throw new RuntimeException("Form channel not found!");
		List<String> questions = form.questions();
		List<String> answers = form.answers();
		StringBuilder formattedForm = new StringBuilder();
		for (int index = 0; index < questions.size(); index++) {
			formattedForm.append("**").append(questions.get(index)).append("**");
			formattedForm.append("\n");
			formattedForm.append(answers.get(index)).append("\n");
		}
		formChannel.sendMessage(formattedForm.toString()).queue();
	}

	public boolean isUsernameInGuild(String username) {
		Guild guild = discordConfigurer.getJda().getGuildById(formConfigurationProperties.guildId());
		if (guild == null) return false;
		List<Member> found = guild.getMembersByName(username, false);
		//* If not empty, someone with that username must exist within the guild, so we return true.
		return !(found.isEmpty());
	}
}
