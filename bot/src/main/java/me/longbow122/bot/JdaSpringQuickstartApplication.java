package me.longbow122.bot;

import lombok.extern.slf4j.Slf4j;
import me.longbow122.bot.configuration.DiscordConfigurer;
import me.longbow122.bot.configuration.properties.DiscordConfigurationProperties;
import me.longbow122.datamodel.repository.CopypastaRepository;
import me.longbow122.bot.service.CopypastaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@SpringBootApplication
@ConfigurationPropertiesScan("me.longbow122.bot.configuration.properties")
@ComponentScan(basePackages = {
    "me.longbow122.datamodel.repository",
    "me.longbow122.bot.service",
    "me.longbow122.bot.configuration",
    "me.longbow122.bot.exception"
})
public class JdaSpringQuickstartApplication implements CommandLineRunner {

    private final DiscordConfigurationProperties discordConfigurationProperties;

    private final CopypastaService copypastaService;

    private final DiscordConfigurer discordConfigurer;

    private final CopypastaRepository copypastaRepository;

    @Autowired
    public JdaSpringQuickstartApplication(DiscordConfigurationProperties discordConfigurationProperties, DiscordConfigurer discordConfigurer, CopypastaService copypastaService, CopypastaRepository copypastaRepository) {
        this.discordConfigurationProperties = discordConfigurationProperties;
        this.discordConfigurer = discordConfigurer;
        this.copypastaRepository = copypastaRepository;
        this.copypastaService = copypastaService;
    }

    public static void main(String[] args) {
        SpringApplication.run(JdaSpringQuickstartApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        discordConfigurer.jda();
    }
}
