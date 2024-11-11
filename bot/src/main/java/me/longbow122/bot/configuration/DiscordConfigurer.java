package me.longbow122.bot.configuration;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.longbow122.bot.configuration.properties.DiscordConfigurationProperties;
import me.longbow122.bot.listener.SlashFormCommandListener;
import me.longbow122.bot.service.FormService;
import me.longbow122.datamodel.repository.entities.Copypasta;
import me.longbow122.bot.listener.CopypastaAutocompleteListener;
import me.longbow122.bot.listener.CopypastaModalListener;
import me.longbow122.bot.listener.SlashCopypastaCommandListener;
import me.longbow122.bot.service.CopypastaService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Slf4j
@Configuration
public class DiscordConfigurer {

    private final DiscordConfigurationProperties discordConfigurationProperties;

    private final CopypastaService copypastaService;

    private final FormService formService;

    @Getter
    private JDA jda;

    @Autowired
    public DiscordConfigurer(DiscordConfigurationProperties discordConfigurationProperties, CopypastaService copypastaService, FormService formService) {
        this.discordConfigurationProperties = discordConfigurationProperties;
        this.copypastaService = copypastaService;
        this.formService = formService;
    }

    @Bean
    @Profile("!test")
    public JDA jda() throws InterruptedException {
        JDA jdaBuild = JDABuilder
                .createDefault(discordConfigurationProperties.botToken())
                .enableIntents(List.of(GatewayIntent.GUILD_MEMBERS))
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setActivity(Activity.customStatus("Use /form for help!"))
                .addEventListeners(new SlashCopypastaCommandListener(copypastaService, discordConfigurationProperties))
                .addEventListeners(new CopypastaAutocompleteListener(copypastaService))
                .addEventListeners(new CopypastaModalListener(copypastaService, formService))
                .addEventListeners(new SlashFormCommandListener())
                .build();

        List<Copypasta> pastas = copypastaService.findAllCopypasta();
        CommandListUpdateAction commands = jdaBuild.updateCommands();
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

        commands.addCommands(Commands.slash("form", "Get a laptop recommendation here!"));
        commands.queue();
        jdaBuild.awaitReady();
        this.jda = jdaBuild;
        //* Some good debug to have when starting up. Decided to keep this.
        log.info(jdaBuild.toString());
        pastas.forEach(pasta -> {
	        log.info("name: {}", pasta.getName());
	        log.info("message: {}", pasta.getMessage());
        });
        if(pastas.isEmpty()) log.info("Pastas was empty");
        return jdaBuild;
    }
}
