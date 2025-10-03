package com.gweneira.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.awt.*;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PollResultCommand extends ListenerAdapter implements SlashCommand {

    private static final String[] EMOJIS = {"1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣"};

    @Override
    public CommandData getCommandData() {
        return Commands.slash("pollresult", "Show the results of a poll 📊")
                .addOption(OptionType.STRING, "message_id", "The ID of the poll message", true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String messageId = event.getOption("poll_id").getAsString();

        event.deferReply().queue();

        event.getChannel().retrieveMessageById(messageId).queue(
                message -> showResults(event, message),
                failure -> event.getHook().sendMessage(":warning: Could not find a message with that ID in this channel!").queue()
        );
    }

    private void showResults(SlashCommandInteractionEvent event, Message message) {
        String title = message.getEmbeds().isEmpty() ? "Poll Results" : message.getEmbeds().get(0).getTitle();
        String description = message.getEmbeds().isEmpty() ? "" : message.getEmbeds().get(0).getDescription();

        StringBuilder results = new StringBuilder();
        List<MessageReaction> reactions = message.getReactions();

        AtomicInteger totalVotes = new AtomicInteger();

        for (int i = 0; i < EMOJIS.length; i++) {
            String emoji = EMOJIS[i];
            reactions.stream()
                    .filter(r -> r.getEmoji().getName().equals(emoji))
                    .findFirst()
                    .ifPresent(r -> {
                        int count = r.getCount() - 1; // exclude bot’s own reaction
                        if (count > 0) {
                            results.append(emoji).append(" — **").append(count).append(" votes**\n");
                            totalVotes.addAndGet(count);
                        }
                    });
        }

        if (results.isEmpty()) {
            results.append(":grey_question: No votes yet!");
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("📊 Results: " + (title != null ? title : "Poll"))
                .setDescription(description != null ? description : "")
                .addField("Votes", results.toString(), false)
                .setColor(new Color(238, 118, 255))
                .setFooter("Total votes: " + totalVotes, event.getUser().getAvatarUrl())
                .setTimestamp(Instant.now());

        event.getHook().sendMessageEmbeds(embed.build()).queue();
    }
}
