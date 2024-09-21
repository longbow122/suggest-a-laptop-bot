package me.longbow122.configuration;

import com.freya02.botcommands.api.CommandsBuilder;
import me.longbow122.configuration.properties.DiscordConfigurationProperties;
import me.longbow122.discord.ExtensionRegister;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DiscordConfigurer {

    private final DiscordConfigurationProperties discordConfigurationProperties;

    private final ExtensionRegister extensionRegister;

    @Bean
    public JDA jda() throws InterruptedException {
        JDA jda =  JDABuilder
                .createDefault(discordConfigurationProperties.botToken())
                .setActivity(Activity.listening("User command"))
                .build();

        jda.awaitReady();
        CommandsBuilder.newBuilder()
                .extensionsBuilder(extensionRegister) // Don't remove this! This is necessary for registering your beans
                .build(jda, "me.longbow122.command");

        return jda;
    }

}
