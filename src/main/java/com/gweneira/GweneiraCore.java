package com.gweneira;

import com.gweneira.commands.SlashCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.reflections.Reflections;

import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GweneiraCore extends ListenerAdapter {

    private final List<SlashCommand> commands;

    public GweneiraCore() {
        // auto-discover all commands in com.gweneira.commands
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

        System.out.println("✅ Loaded commands: " +
                commands.stream().map(c -> c.getCommandData().getName()).toList());
    }

    public static void main(String[] args) throws Exception {
        String token = TokenLoader.loadToken();
        String mode = TokenLoader.loadMode();
        String guildId = TokenLoader.loadGuildId();

        GweneiraCore core = new GweneiraCore();

        JDABuilder builder = JDABuilder.createDefault(token,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT,
                GatewayIntent.GUILD_MESSAGE_REACTIONS
        );

        // all possible activities
        List<Activity> activities = List.of(
                Activity.playing("with snowflakes ❄️"),
                Activity.watching("over Gweneira's magic ✨"),
                Activity.listening("to lo-fi beats 🎶"),
                Activity.competing("the study tournament 🏆"),
                Activity.customStatus("/gweneira for helps 💖")
        );

        Random random = new Random();
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        builder.addEventListeners(core)
                .setActivity(Activity.customStatus("/gweneira for helps 💖")); // initial status

        // register commands that are also event listener
        for (SlashCommand cmd : core.commands) {
            if (cmd instanceof ListenerAdapter listener) {
                builder.addEventListeners(listener);
                System.out.println("🔌 Added event listener for: " +
                        cmd.getCommandData().getName());
            }
        }

        JDA jda = builder.build();
        jda.awaitReady();

        //schedule random activity updates every 1 minute
        ScheduledExecutorService scheduling = Executors.newSingleThreadScheduledExecutor();

        scheduling.scheduleAtFixedRate(new Runnable() {
            private int index = 0; // track current position

            @Override
            public void run() {
                Activity nextActivity = activities.get(index);
                jda.getPresence().setActivity(nextActivity);
                System.out.println("🔄 Changed activity to: " + nextActivity.getName());

                // move to next, loop back when reaching the end
                index = (index + 1) % activities.size();
            }
        }, 0, 30, TimeUnit.SECONDS); // starts immediately, updates every 1 minute

        // register slash commands
        if ("dev".equals(mode) && guildId != null && !guildId.isEmpty()) {
            System.out.println("⚡ Dev mode: Clearing global & setting guild commands...");

            jda.updateCommands().addCommands().queue(success ->
                    System.out.println("🧹 Cleared ALL global commands."));

            jda.getGuildById(guildId).updateCommands().addCommands().queue(success ->
                    System.out.println("🧹 Cleared guild commands for " + guildId));

            jda.getGuildById(guildId).updateCommands()
                    .addCommands(core.commands.stream().map(SlashCommand::getCommandData).toList())
                    .queue(success ->
                            System.out.println("✅ Registered guild commands: " +
                                    core.commands.stream().map(c -> c.getCommandData().getName()).toList())
                    );

        } else {
            System.out.println("🌍 Prod mode: Clearing guilds & setting global commands...");

            jda.getGuilds().forEach(guild ->
                    guild.updateCommands().addCommands().queue(success ->
                            System.out.println("🧹 Cleared guild commands in " + guild.getName()))
            );

            jda.updateCommands().addCommands().queue(success ->
                    System.out.println("🧹 Cleared ALL global commands."));

            jda.updateCommands()
                    .addCommands(core.commands.stream().map(SlashCommand::getCommandData).toList())
                    .queue(success ->
                            System.out.println("✅ Registered global commands: " +
                                    core.commands.stream().map(c -> c.getCommandData().getName()).toList())
                    );
        }
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