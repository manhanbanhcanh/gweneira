package com.gweneira.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.time.Instant;

public class PollCommand extends ListenerAdapter implements SlashCommand {

    private static final String[] EMOJIS = {"1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣"};

    @Override
    public CommandData getCommandData() {
        return Commands.slash("poll", "Create a poll 📊")
                .addOption(OptionType.STRING, "question", "The poll question", true)
                .addOption(OptionType.STRING, "option1", "First option", true)
                .addOption(OptionType.STRING, "option2", "Second option", true)
                .addOption(OptionType.STRING, "option3", "Third option", false)
                .addOption(OptionType.STRING, "option4", "Fourth option", false)
                .addOption(OptionType.STRING, "option5", "Fifth option", false);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String question = event.getOption("question").getAsString();

        StringBuilder description = new StringBuilder();
        for (int i = 1; i <= EMOJIS.length; i++) {
            if (event.getOption("option" + i) != null) {
                description.append(EMOJIS[i - 1])
                        .append(" ")
                        .append(event.getOption("option" + i).getAsString())
                        .append("\n");
            }
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("📊 " + question)
                .setDescription(description.toString())
                .setColor(new Color(118, 255, 251))
                .setTimestamp(Instant.now())
                .setFooter("Poll created by " + event.getUser().getName(),
                        event.getUser().getAvatarUrl());

        event.deferReply().setEphemeral(false).queue();
        event.getHook().sendMessageEmbeds(embed.build())
                .addActionRow(
                        Button.primary("poll:results", "📑 View Results"),
                        Button.danger("poll:close", "🛑 Close Poll")
                )
                .queue(message -> {
                    for (int i = 0; i < description.toString().split("\n").length; i++) {
                        message.addReaction(Emoji.fromUnicode(EMOJIS[i])).queue();
                    }
                });
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String buttonId = event.getComponentId();

        if (buttonId.equals("poll:results")) {
            // ✅ Re-fetch message with updated reactions
            event.getMessage().getChannel().retrieveMessageById(event.getMessageId()).queue(message -> {
                sendPollResults(event, message);
            });
        }

        if (buttonId.equals("poll:close")) {
            User user = event.getUser();
            String pollCreator = event.getMessage().getEmbeds().get(0).getFooter() != null
                    ? event.getMessage().getEmbeds().get(0).getFooter().getText().replace("Poll created by ", "")
                    : "";

            // Only creator can close
            if (!user.getName().equals(pollCreator)) {
                event.reply(":warning: Only the poll creator can close this poll!").setEphemeral(true).queue();
                return;
            }

            event.getMessage().getChannel().retrieveMessageById(event.getMessageId()).queue(message -> {
                // Clear reactions to stop voting
                message.clearReactions().queue();

                EmbedBuilder closed = new EmbedBuilder(message.getEmbeds().get(0))
                        .setTitle("📊 [CLOSED] " + message.getEmbeds().get(0).getTitle())
                        .setColor(Color.GRAY);

                message.editMessageEmbeds(closed.build())
                        .setComponents() // clears all buttons
                        .queue();
                // remove buttons
                event.reply("🛑 Poll closed.").setEphemeral(true).queue();
            });
        }
    }

    private void sendPollResults(ButtonInteractionEvent event, Message message) {
        StringBuilder results = new StringBuilder();

        message.getReactions().forEach(reaction -> {
            String emoji = reaction.getEmoji().getName();
            int count = reaction.getCount() - 1; // subtract bot
            results.append(emoji).append(" — **").append(count).append(" votes**\n");
        });

        if (results.isEmpty()) {
            results.append("No votes yet.");
        }

        EmbedBuilder resultEmbed = new EmbedBuilder()
                .setTitle("📊 Poll Results")
                .setDescription(results.toString())
                .setColor(new Color(238, 118, 255))
                .setTimestamp(Instant.now());

        event.replyEmbeds(resultEmbed.build()).setEphemeral(true).queue();
    }
}