package com.gweneira.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class GweneiraCommand implements SlashCommand {
    @Override
    public CommandData getCommandData(){
        return Commands.slash("gweneira","Show Gweneira's magical link ✨");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.reply("https://bento.me/ducmanh").queue();
    }
}
