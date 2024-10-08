package me.longbow122;

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

@SpringBootApplication
@ConfigurationPropertiesScan("me.longbow122.configuration.properties")
@ComponentScan(basePackages = {
    "me.longbow122.datamodel.repository",
    "me.longbow122.service",
    "me.longbow122.configuration"
})
public class JdaSpringQuickstartApplication implements CommandLineRunner {

    //TODO APPLICATION PROPERTIES FILE CAN BE USED EXTERNALLY, AND WE SEEM TO HAVE GOTTEN THAT PART WORKING, BUT
    // WE ARE HAVING ISSUES WITH THE INFORMATION WE ARE GIVING FOR THE CONNECTION DETAILS OF THE DATABASE.
    // NEED TO LOOK INTO IT AND FIX THAT, SINCE USING AN APPLICATION PROPERTIES FILE EXTERNALLY FOR NOW WORKS.

    //TODO IN THE FUTURE, WE NEED TO ENSURE THAT WE ARE NOT RELIANT ON A BOT TOKEN FOR TESTS, THAT IS BAD.
    // SHOULD MAYBE USE THE CONFIG SYSTEM WE HAD IMPLEMENTED IN THE OLD BOT?

    //TODO ALSO NEED TO ENSURE THAT WE CREATE A FLATFILE DB IN THE SAME DIRECTORY AS THE JAR WHEN RUNNING. DO WE NEED TO COMPILE DATA-MODEL?
    // FLATFILE MUST BE CREATED WITH TABLE INITIALISED ON STARTUP. NEED TO FINDOUT HOW WE ARE GOING TO GET THAT PART DONE.

    //TODO NEED TO ENSURE THAT DATA-MODEL JAR IS JUST A LIBRARY JAR, WE NEED TO BE COMPILING IT WITHIN BOT MODULE! API CAN RUN SEPARATELY IF NEEDED, BUT NEEDS TO BE REFERRING TO
    // BOT MODULE METHODS

    //TODO NEED TO IMPLEMENT ALL COPYPASTA FUNCTIONALITY AS WELL.
    // DATA-MODEL METHODS SHOULD BE HANDLING ALL CALLS AND WORK TO THE DB FLATFILE, WHICH NEEDS TO BE CREATED WHEN THE DB STARTS.

    //TODO NEED TO FULLY TEST COPYPASTA FUNCTIONALITY BEFORE WORKING ON MERGING ENTIRE PR FOR BOT LAYER

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
    }
}
