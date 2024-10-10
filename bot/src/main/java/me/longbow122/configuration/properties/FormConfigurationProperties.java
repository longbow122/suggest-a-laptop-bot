package me.longbow122.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "form")
public record FormConfigurationProperties(
	long guildId,
	long formChannelId) {
}
