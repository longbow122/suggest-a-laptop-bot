package me.longbow122.bot.configuration;

public record DiscordConfiguration(
  String botToken,
  long copypastaRoleID,
  long adminRoleID) {
}
