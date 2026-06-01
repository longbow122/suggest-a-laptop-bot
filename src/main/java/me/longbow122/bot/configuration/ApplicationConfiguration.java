package me.longbow122.bot.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public record ApplicationConfiguration(
  DiscordConfiguration discord,
  DatabaseConfiguration database
) {

  public static ApplicationConfiguration load(String[] args) {
    Properties fileProperties = loadApplicationProperties();
    Map<String, String> commandLine = parseCommandLineArgs(args);

    String botToken = required(value(
      commandLine,
      fileProperties,
      List.of("discord.bot-token", "discord.botToken"),
      List.of("DISCORD_BOT_TOKEN"),
      null
    ), "discord.bot-token / DISCORD_BOT_TOKEN");

    long copypastaRoleId = requiredLong(value(
      commandLine,
      fileProperties,
      List.of("discord.copypasta-role-id", "discord.copypastaRoleID", "discord.copypastaRoleId"),
      List.of("DISCORD_COPYPASTA_ROLE_ID", "DISCORD_COPYPASTA_ROLEID"),
      null
    ), "discord.copypasta-role-id / DISCORD_COPYPASTA_ROLE_ID");

    long adminRoleId = requiredLong(value(
      commandLine,
      fileProperties,
      List.of("discord.admin-role-id", "discord.adminRoleID", "discord.adminRoleId"),
      List.of("DISCORD_ADMIN_ROLE_ID", "DISCORD_ADMIN_ROLEID"),
      null
    ), "discord.admin-role-id / DISCORD_ADMIN_ROLE_ID");

    String databaseUrl = value(
      commandLine,
      fileProperties,
      List.of("database.url"),
      List.of("DATABASE_URL"),
      "jdbc:h2:file:./data"
    );

    String databaseUsername = value(
      commandLine,
      fileProperties,
      List.of("database.username"),
      List.of("DATABASE_USERNAME"),
      "sa"
    );

    String databasePassword = value(
      commandLine,
      fileProperties,
      List.of("database.password"),
      List.of("DATABASE_PASSWORD"),
      "password"
    );

    return new ApplicationConfiguration(
      new DiscordConfiguration(botToken, copypastaRoleId, adminRoleId),
      new DatabaseConfiguration(databaseUrl, databaseUsername, databasePassword)
    );
  }

  private static Properties loadApplicationProperties() {
    Properties properties = new Properties();
    try (InputStream input = ApplicationConfiguration.class.getClassLoader().getResourceAsStream("application.properties")) {
      if (input != null) properties.load(input);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to load classpath application.properties", e);
    }

    Path externalProperties = Path.of("application.properties");
    if (Files.isRegularFile(externalProperties)) {
      try (InputStream input = Files.newInputStream(externalProperties)) {
        properties.load(input);
      } catch (IOException e) {
        throw new IllegalStateException("Unable to load application.properties", e);
      }
    }

    return properties;
  }

  private static Map<String, String> parseCommandLineArgs(String[] args) {
    Map<String, String> parsed = new HashMap<>();
    if (args == null) return parsed;

    for (String arg : args) {
      if (!arg.startsWith("--")) continue;
      int separator = arg.indexOf('=');
      if (separator <= 2) continue;

      parsed.put(arg.substring(2, separator), arg.substring(separator + 1));
    }

    return parsed;
  }

  private static String value(
    Map<String, String> commandLine,
    Properties fileProperties,
    List<String> propertyKeys,
    List<String> environmentKeys,
    String defaultValue
  ) {
    for (String key : propertyKeys) {
      if (commandLine.containsKey(key)) return commandLine.get(key);
    }

    for (String key : propertyKeys) {
      String systemProperty = System.getProperty(key);
      if (systemProperty != null) return systemProperty;
    }

    for (String key : environmentKeys) {
      String environmentValue = System.getenv(key);
      if (environmentValue != null) return environmentValue;
    }

    for (String key : propertyKeys) {
      String fileValue = fileProperties.getProperty(key);
      if (fileValue != null) return fileValue;
    }

    return defaultValue;
  }

  private static String required(String value, String name) {
    if (value == null || value.isBlank()) {
      throw new IllegalStateException("Missing required configuration value: " + name);
    }
    return value;
  }

  private static long requiredLong(String value, String name) {
    String required = required(value, name);
    try {
      return Long.parseLong(required);
    } catch (NumberFormatException e) {
      throw new IllegalStateException("Configuration value must be a long: " + name, e);
    }
  }
}
