package com.gweneira.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public interface SlashCommand {
    CommandData getCommandData();
    void execute(SlashCommandInteractionEvent event);
}
