package me.longbow122.bot;

import me.longbow122.bot.configuration.ApplicationConfiguration;
import me.longbow122.bot.configuration.DiscordConfigurer;
import me.longbow122.bot.repository.CopypastaRepository;
import me.longbow122.bot.service.CopypastaService;
import net.dv8tion.jda.api.JDA;

public class SuggestALaptopBotApplication {

  public static void main(String[] args) {
    try {
      ApplicationConfiguration configuration = ApplicationConfiguration.load(args);

      CopypastaRepository copypastaRepository = new CopypastaRepository(
        configuration.database().url(),
        configuration.database().username(),
        configuration.database().password()
      );
      CopypastaService copypastaService = new CopypastaService(copypastaRepository);
      JDA jda = new DiscordConfigurer(configuration.discord(), copypastaService).start();

      Runtime.getRuntime().addShutdownHook(new Thread(jda::shutdown, "jda-shutdown"));
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("Interrupted while starting Discord bot", e);
    }
  }
}
