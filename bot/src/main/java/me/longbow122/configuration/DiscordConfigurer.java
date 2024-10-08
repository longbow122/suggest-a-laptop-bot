package me.longbow122.configuration;

import lombok.extern.slf4j.Slf4j;
import me.longbow122.configuration.properties.DiscordConfigurationProperties;
import me.longbow122.datamodel.repository.entities.Copypasta;
import me.longbow122.listener.CopypastaAutocompleteListener;
import me.longbow122.listener.CopypastaModalListener;
import me.longbow122.listener.SlashCopypastaCommandListener;
import me.longbow122.service.CopypastaService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Slf4j
@Configuration
public class DiscordConfigurer {

    private final DiscordConfigurationProperties discordConfigurationProperties;

    private final CopypastaService copypastaService;

    public DiscordConfigurer(DiscordConfigurationProperties discordConfigurationProperties, CopypastaService copypastaService) {
        this.discordConfigurationProperties = discordConfigurationProperties;
        this.copypastaService = copypastaService;
    }


    @Bean
    public JDA jda() throws InterruptedException {
        JDA jda = JDABuilder
                .createDefault(discordConfigurationProperties.botToken())
                .setActivity(Activity.customStatus("Use /form for help!"))
                .addEventListeners(new SlashCopypastaCommandListener(copypastaService))
                .addEventListeners(new CopypastaAutocompleteListener(copypastaService))
                .addEventListeners(new CopypastaModalListener(copypastaService))
                .build();

        List<Copypasta> pastas = copypastaService.findAllCopypasta();
        CommandListUpdateAction commands = jda.updateCommands();
        pastas.forEach(copypasta -> commands.addCommands(Commands.slash(copypasta.getName(), copypasta.getDescription())
            .setGuildOnly(true)));

        commands.addCommands(Commands.slash("copypasta", "Modify the copypastas available.")
            .addSubcommands(new SubcommandData("add", "Add a copypasta to the list of slash commands"))
            .addSubcommands(new SubcommandData("remove", "Remove a copypasta from the list of slash commands")
                .addOption(OptionType.STRING, "name", "The name of the copypasta command to be removed. REQUIRED.", true, true))

            .addSubcommands(new SubcommandData("update", "Change the name, description or message of an existing copypasta")
                .addOption(OptionType.STRING, "name", "The current name of the copypasta command to be updated. REQUIRED.", true, true)
                .addOption(OptionType.STRING, "field", "The field to update. (Name, Description, Message). REQUIRED.", true, true)
                .addOption(OptionType.STRING, "value", "The new value of the field. REQUIRED.", true)));
        commands.queue();
        jda.awaitReady();
        return jda;
    }
}
