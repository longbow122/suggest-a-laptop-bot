package me.longbow122.bot.configuration;

public record DatabaseConfiguration(
  String url,
  String username,
  String password
) {
}
