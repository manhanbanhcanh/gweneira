package com.gweneira.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.awt.*;

public class GweneiraCommand implements SlashCommand {

    @Override
    public CommandData getCommandData() {
        return Commands.slash("gweneira", "Show Gweneira’s magical help menu ✨");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("❄️ Gweneira Bot Commands ❄️")
                .setDescription(
                        """
                                Here’s everything I can do ✨\n
                                **/gweneira** → Show this magical help menu  
                                **/study** → Start, pause, resume, and stop a study session 📝  
                                **/reminder** → Set a reminder (DM or channel) ⏰  
                                **/poll** → Create a poll 📊 
                                **/pollresult → Show the results of a poll 📊 
                                
                                More commands coming soon... 🌸
                                """
                )
                .setColor(new Color(118, 255, 251))
                .setFooter("Gweneira Bot • Made with ❤️", event.getJDA().getSelfUser().getEffectiveAvatarUrl());

        embed.addField("🍓 About me", "[Click here](https://season-wholesaler-da0.notion.site/Gweneira-Discord-Bot-278350b3eff08027b226e20718b9a8ad?source=copy_link)", true);
        embed.addField("✨ About my creator", "[Click here](https://bento.me/ducmanh)", true);

        event.replyEmbeds(embed.build()).queue();
    }
}