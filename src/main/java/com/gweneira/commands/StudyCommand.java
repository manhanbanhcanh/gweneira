package com.gweneira.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class StudyCommand extends ListenerAdapter implements SlashCommand {

    private static final String STOP_EMOJI = "⏹️";
    private static final String PAUSE_EMOJI = "⏸️";
    private static final String RESUME_EMOJI = "▶️";

    private static final String LOVELY_EMOJI ="💞";

    //track active sessions by message ID
    private final Map<Long, SessionData> activeSessions = new HashMap<>();

    @Override
    public net.dv8tion.jda.api.interactions.commands.build.CommandData getCommandData() {
        return Commands.slash("study", "Start, pause, resume, and stop a study session with reactions");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("📖 Study Session Started")
                .setDescription(
                        "React with:\n" +
                                PAUSE_EMOJI + " to pause\n" +
                                RESUME_EMOJI + " to continue\n" +
                                STOP_EMOJI + " to stop"
                )
                .setColor(new Color(118,255,251))
                .setTimestamp(Instant.now());

        event.getHook().sendMessageEmbeds(embed.build()).queue(message -> {
            activeSessions.put(message.getIdLong(), new SessionData());

            message.addReaction(Emoji.fromUnicode(PAUSE_EMOJI)).queue();
            message.addReaction(Emoji.fromUnicode(RESUME_EMOJI)).queue();
            message.addReaction(Emoji.fromUnicode(STOP_EMOJI)).queue();
        });
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (event.getUser() == null || event.getUser().isBot()) return;

        Message message = event.retrieveMessage().complete();
        Long messageId = message.getIdLong();

        if (!activeSessions.containsKey(messageId)) return;
        String emoji = event.getReaction().getEmoji().getName();

        SessionData session = activeSessions.get(messageId);

        switch (emoji) {
            case PAUSE_EMOJI -> {
                if (!session.isPaused()) {
                    session.pause();
                    updateEmbed(message, "⏸️ Study Session Paused", Color.ORANGE);
                }
            }
            case RESUME_EMOJI -> {
                if (session.isPaused()) {
                    session.resume();
                    updateEmbed(message, "▶️ Study Session Resumed", new Color(118,255,251));
                }
            }
            case STOP_EMOJI -> {
                session.stop();
                long totalSeconds = session.getTotalDurationSeconds();
                String formatted = formatDuration(totalSeconds);

                EmbedBuilder finished = new EmbedBuilder()
                        .setTitle("🧠🌟 Study Session Ended")
                        .setDescription("This session lasted **" + formatted + "**.")
                        .setColor(new Color(238,118,255))
                        .setTimestamp(Instant.now());

                message.editMessageEmbeds(finished.build()).queue();
                message.clearReactions().queue();
                activeSessions.remove(messageId);
                message.addReaction(Emoji.fromUnicode(LOVELY_EMOJI)).queue();
            }
        }

        event.getReaction().removeReaction(event.getUser()).queue();
    }

    private void updateEmbed(Message message, String title, Color color) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(title)
                .setDescription(
                        "React with:\n" +
                                PAUSE_EMOJI + " to pause\n" +
                                RESUME_EMOJI + " to continue\n" +
                                STOP_EMOJI + " to stop"
                )
                .setColor(color)
                .setTimestamp(Instant.now());

        message.editMessageEmbeds(embed.build()).queue();
    }

    private String formatDuration(long seconds) {
        Duration d = Duration.ofSeconds(seconds);
        long hours = d.toHours();
        long minutes = d.toMinutesPart();
        long secs = d.toSecondsPart();

        if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes, secs);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, secs);
        } else {
            return String.format("%ds", secs);
        }
    }

    // --- SessionData class ---
    private static class SessionData {
        private Instant start;
        private Duration accumulated;
        private boolean paused;

        public SessionData() {
            this.start = Instant.now();
            this.accumulated = Duration.ZERO;
            this.paused = false;
        }

        public void pause() {
            if (!paused) {
                accumulated = accumulated.plus(Duration.between(start, Instant.now()));
                paused = true;
            }
        }

        public void resume() {
            if (paused) {
                start = Instant.now();
                paused = false;
            }
        }

        public void stop() {
            if (!paused) {
                accumulated = accumulated.plus(Duration.between(start, Instant.now()));
            }
            paused = true;
        }

        public long getTotalDurationSeconds() {
            return accumulated.toSeconds();
        }

        public boolean isPaused() {
            return paused;
        }
    }
}