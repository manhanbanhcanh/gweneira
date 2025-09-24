package com.gweneira;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import java.io.File;

public class Bot extends ListenerAdapter {

    public static void main(String[] args) throws Exception {
        String token = loadToken();

        JDABuilder.createDefault(token,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.DIRECT_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new Bot())
                .setActivity(Activity.playing("With Gweneira ❄️"))
                .build();
    }

    private static String loadToken() throws Exception {
        File xmlFile = new File("src/main/resources/config.xml");
        //check the config.xml file
        if (!xmlFile.exists()) {
            throw new IllegalStateException(
                    "config.xml not found at: " + xmlFile.getAbsolutePath() +
                    "\nPlease copy src/main/resources/config.example.xml to config.xml " +
                    "and put your bot token inside."
            );
        }

        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(xmlFile);
        doc.getDocumentElement().normalize();
        //check if there is any token in config.xml
        if (doc.getElementsByTagName("token").getLength() == 0) {
            throw new IllegalStateException("No <token> element found in config.xml");
        }

        return doc.getElementsByTagName("token")
                .item(0)
                .getTextContent()
                .trim();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        if (event.getAuthor().isBot()) return;

        String message = event.getMessage().getContentRaw();

        if (message.startsWith("!gweneira")) {
            event.getChannel().sendMessage("https://bento.me/ducmanh").queue();
        }
    }

}
