package com.gweneira;

import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class TokenLoader {

    public static String loadToken() throws Exception {
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

        if (doc.getElementsByTagName("token").getLength() == 0) {
            throw new IllegalStateException(
                    "⚠️ No <token> element found in config.xml!\n" +
                            "Make sure your config.xml looks like config.example.xml."
            );
        }

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
}
