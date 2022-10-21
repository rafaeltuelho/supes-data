package org.acme;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CharacterScraper {

    private CharacterScraper() {
        // Avoid direct instantiation.
    }

    static int extractLevel(Document page) {
        Elements levelComponent = page.select(".profile-portrait-holder a");
        int level = -1;
        String levelTxt = levelComponent.text();
        if (!levelTxt.isBlank()) {
            if (levelTxt.contains("K")) {
                levelTxt = levelTxt.replace("K", "");
                level = (int) Float.parseFloat(levelTxt) * 1000;
            } else if (levelTxt.contains("M")) {
                levelTxt = levelTxt.replace("M", "");
                level = (int) Float.parseFloat(levelTxt) * 1000000;
            } else {
                try {
                    level = Integer.parseInt(levelTxt);
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }
        }
        return level;
    }

    static String extractAlignment(Document document) {
        Element element = document.getElementsByTag("td").stream().filter(e -> e.text().equalsIgnoreCase("alignment"))
                .findFirst().orElse(null);

        if (element != null) {
            Element parent = element.parent();
            if (parent != null) {
                Element td = parent.select("td").last();
                if (td != null) {
                    return td.text();
                }
            }

        }
        return null;
    }

    static List<String> extractPowers(Document document) {
        List<String> powers = new ArrayList<>();
        for (Element element : document.select(".fa-bolt")) {
            Element parent = element.parent();
            if (parent != null) {
                String text = parent.text();
                if (!"super powers".equalsIgnoreCase(text) && !"powers".equalsIgnoreCase(text)) {
                    powers.add(text);
                }
            }
        }
        return powers;
    }
    
    public static String extractPicture(Document document) {
        Elements select = document.select(".portrait img");
        return Constants.SUPER_DB_ROOT + select.attr("src");
    }
}
