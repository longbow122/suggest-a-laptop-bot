package me.longbow122.bot.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "discord")
public record DiscordConfigurationProperties(String botToken) {
}
