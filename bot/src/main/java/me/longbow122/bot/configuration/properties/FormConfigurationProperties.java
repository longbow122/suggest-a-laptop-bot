package me.longbow122.bot.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "form")
public record FormConfigurationProperties(
	long guildId) {
}
