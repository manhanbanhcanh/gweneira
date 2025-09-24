package com.gweneira;

import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class TokenLoader {
    private static Document loadDocument() throws Exception {
        File xmlFile = new File("src/main/resources/config.xml");

        if (!xmlFile.exists()) {
            throw new IllegalStateException(
                    "⚠️ config.xml not found!\n" +
                            "Please copy src/main/resources/config.example.xml to config.xml " +
                            "and put your bot token inside."
            );
        }

        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(xmlFile);
        doc.getDocumentElement().normalize();
        return doc;
    }

    public static String loadToken() throws Exception {
        Document doc = loadDocument();

        String token = doc.getElementsByTagName("token")
                .item(0)
                .getTextContent()
                .trim();

        if (token.isEmpty() || token.equals("YOUR-BOT-TOKEN-HERE")) {
            throw new IllegalStateException(
                    "⚠️ Your config.xml still has the placeholder token!\n" +
                            "Please edit it and put your real Discord bot token."
            );
        }

        return token;
    }

    public static String loadGuildId() throws Exception {
        Document doc = loadDocument();
        if (doc.getElementsByTagName("guildId").getLength() == 0) {
            return null;
        }
        return doc.getElementsByTagName("guildId").item(0).getTextContent().trim();
    }

    public static String loadMode() throws Exception {
        Document doc = loadDocument();
        if (doc.getElementsByTagName("mode").getLength() == 0) {
            return "prod";
        }
        return doc.getElementsByTagName("mode").item(0).getTextContent().trim().toLowerCase();
    }
}