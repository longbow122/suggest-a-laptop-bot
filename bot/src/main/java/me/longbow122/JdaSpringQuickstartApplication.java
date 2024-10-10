package me.longbow122;

import lombok.extern.slf4j.Slf4j;
import me.longbow122.configuration.DiscordConfigurer;
import me.longbow122.configuration.properties.DiscordConfigurationProperties;
import me.longbow122.datamodel.repository.CopypastaRepository;
import me.longbow122.service.CopypastaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@SpringBootApplication
@ConfigurationPropertiesScan("me.longbow122.configuration.properties")
@ComponentScan(basePackages = {
    "me.longbow122.datamodel.repository",
    "me.longbow122.service",
    "me.longbow122.configuration"
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

    //TODO THIS IS A MINOR ISSUE, BUT WE CANNOT SEEM TO RUN THINGS THROUGH THE IDE. NEED TO LOOK INTO WHY THIS IS.
    //? This is a minor issue, since we can build it and run things as intended easily.
    //? Runs fine in production with a real environment, but does not run as intended on the IDE, unsure why.
    @Override
    public void run(String... args) throws Exception {
        discordConfigurer.jda();
        //TODO DEBUG NEEDS TO GO
        log.info("We should see this if we have the Bot running");
    }
}
