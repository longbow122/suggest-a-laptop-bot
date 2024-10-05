package me.longbow122.configuration;

import com.freya02.botcommands.api.CommandsBuilder;
import me.longbow122.configuration.properties.DiscordConfigurationProperties;
import me.longbow122.datamodel.repository.entities.Copypasta;
import me.longbow122.discord.ExtensionRegister;
import lombok.RequiredArgsConstructor;
import me.longbow122.listener.CopypastaAutocompleteListener;
import me.longbow122.listener.CopypastaModalListener;
import me.longbow122.listener.SlashCopypastaCommandListener;
import me.longbow122.service.CopypastaService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DiscordConfigurer {

    private final DiscordConfigurationProperties discordConfigurationProperties;

    private final ExtensionRegister extensionRegister;

    @Autowired
    private CopypastaService copypastaService;

    @Bean
    public JDA jda() throws InterruptedException {
        //TODO THIS NEEDS TO BE AMENEDED AND ADDED ONCE WE HAVE GOT THE BOT TO A RUNNING STATE
        // NEED TO ENSURE THAT WE HAVE THE CONFIG FILE WORKING WITH THE TOKEN AS INTENDED FIRST
        // ALSO NEED TO ENSURE THAT THE DATABASE APPEARS AS INTENDED RIGHT NEXT TO THIS JAR SO THAT WE CAN ACCESS IT AS WELL.
        /*JDA jda =  JDABuilder
                .createDefault(discordConfigurationProperties.botToken())
                .setActivity(Activity.listening("User command"))
                .addEventListeners(new SlashCopypastaCommandListener(copypastaService))
                .addEventListeners(new CopypastaAutocompleteListener(copypastaService))
                .addEventListeners(new CopypastaModalListener(copypastaService))
                .build();

        List<Copypasta> pastas = copypastaService.findAllCopypasta();
        CommandListUpdateAction commands = jda.updateCommands();
        pastas.forEach(copypasta -> commands.addCommands(Commands.slash(copypasta.getName(), copypasta.getDescription())
            .setGuildOnly(true)));

        jda.awaitReady();
        CommandsBuilder.newBuilder()
                .extensionsBuilder(extensionRegister) // Don't remove this! This is necessary for registering your beans
                .build(jda, "me.longbow122.command");

        return jda;*/
        return null;
    }

}
