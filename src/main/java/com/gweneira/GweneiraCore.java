package com.gweneira;

import com.gweneira.commands.SlashCommand;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.reflections.Reflections;

import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;

public class GweneiraCore extends ListenerAdapter {

    private final List<SlashCommand> commands;

    public GweneiraCore() {
        //auto-discover all commands in com.gweneira.commands
        Reflections reflections = new Reflections("com.gweneira.commands");
        Set<Class<? extends SlashCommand>> commandClasses = reflections.getSubTypesOf(SlashCommand.class);

        this.commands = commandClasses.stream()
                .map(c -> {
                    try {
                        return c.getDeclaredConstructor().newInstance();
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to load command: " + c.getName(), e);
                    }
                })
                .collect(Collectors.toList());
    }

    public static void main(String[] args) throws Exception {
        String token = TokenLoader.loadToken(); // your existing token loader

        GweneiraCore core = new GweneiraCore();

        JDABuilder builder = JDABuilder.createDefault(token,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT
        );

        builder.addEventListeners(core)
                .setActivity(Activity.playing("With Gweneira ❄️"));

        var jda = builder.build();

        //register slash commands
        jda.updateCommands().addCommands(
                core.commands.stream().map(SlashCommand::getCommandData).toList()
        ).queue();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        for (SlashCommand cmd : commands) {
            if (event.getName().equals(cmd.getCommandData().getName())) {
                cmd.execute(event);
                return;
            }
        }
    }
}
