package me.longbow122.service;

import lombok.extern.slf4j.Slf4j;
import me.longbow122.configuration.DiscordConfigurer;
import me.longbow122.configuration.properties.FormConfigurationProperties;
import me.longbow122.dto.FormDTO;
import me.longbow122.exception.exceptions.ChannelNotFoundException;
import me.longbow122.exception.exceptions.GuildNotFoundException;
import me.longbow122.exception.exceptions.UserNotFoundException;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
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
		Guild guild = discordConfigurer.getJda().getGuildById(formConfigurationProperties.guildId());
		if (guild == null) {
			throw new GuildNotFoundException("Guild not found! Check your configuration!");
		}
		//* Thanks to Discord and globally unique usernames, this should ideally be of size 1 every time.
		//* As such, we always get the first one. We don't have access to user IDs in this case, not without discord logins.
		List<Member> found = guild.getMembersByName(form.poster(), false);
		if (found.isEmpty()) {
			throw new UserNotFoundException("Member not found! They must not be in the discord server!");
		}
		TextChannel formChannel = discordConfigurer.getJda().getTextChannelById(form.channelSendID());
		if (formChannel == null) {
			throw new ChannelNotFoundException("Form channel not found! Please check the relevant forms and the requests they make!");
		}
		List<String> questions = form.questions();
		List<String> answers = form.answers();
		StringBuilder formattedForm = new StringBuilder();
		for (int index = 0; index < questions.size(); index++) {
			formattedForm.append("**").append(questions.get(index)).append("**");
			formattedForm.append("\n");
			formattedForm.append(answers.get(index)).append("\n");
		}
		formattedForm.append("**Posted by: **").append(found.getFirst().getAsMention());
		formChannel.sendMessage(formattedForm.toString()).queue();
	}
}
