package com.gweneira.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ReminderCommand extends ListenerAdapter implements SlashCommand {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public CommandData getCommandData() {
        return Commands.slash("reminder", "Set a reminder ⏰")
                .addOptions(
                        new OptionData(OptionType.STRING, "time", "How long to wait (e.g., 10s, 5m, 2h)", true),
                        new OptionData(OptionType.STRING, "message", "The reminder message", true),
                        new OptionData(OptionType.STRING, "delivery", "Where to send the reminder")
                                .addChoice("Direct Message (DM)", "dm")
                                .addChoice("Same Channel", "channel")
                );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        OptionMapping timeOption = event.getOption("time");
        OptionMapping messageOption = event.getOption("message");
        OptionMapping deliveryOption = event.getOption("delivery");

        if (timeOption == null || messageOption == null) {
            event.reply("⚠️ You must provide both a time and a message!").setEphemeral(true).queue();
            return;
        }

        String timeInput = timeOption.getAsString();
        String reminderMessage = messageOption.getAsString();
        String delivery = (deliveryOption != null) ? deliveryOption.getAsString().toLowerCase() : "dm";

        long delay = parseTime(timeInput);
        if (delay <= 0) {
            event.reply("⚠️ Invalid time format! Use something like `10s`, `5m`, or `2h`.").setEphemeral(true).queue();
            return;
        }

        event.reply("✅ Reminder set for **" + timeInput + "**. I’ll remind you in `" + delivery + "`.")
                .setEphemeral(true)
                .queue();

        //schedule reminder
        scheduler.schedule(() -> {
            String reminderText = "⏰ Reminder: " + reminderMessage;

            if ("channel".equals(delivery)) {
                // Send to the same channel
                event.getChannel().sendMessage(event.getUser().getAsMention() + " " + reminderText).queue();
            } else {
                // Send as DM
                event.getUser().openPrivateChannel().queue(channel ->
                        channel.sendMessage(reminderText).queue()
                );
            }
        }, delay, TimeUnit.SECONDS);
    }

    private long parseTime(String input) {
        try {
            char unit = input.charAt(input.length() - 1);
            long value = Long.parseLong(input.substring(0, input.length() - 1));

            return switch (unit) {
                case 's' -> value;               // seconds
                case 'm' -> value * 60;          // minutes
                case 'h' -> value * 3600;        // hours
                default -> -1;
            };
        } catch (Exception e) {
            return -1;
        }
    }
}