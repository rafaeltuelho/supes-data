package org.acme;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PowerScraper {
    
    private PowerScraper() {
        // Avoid direct instantiation.
    }

    static Power extractSuperPower(Document document) {
        Power power = new Power();
        String name = document.body().getElementsByTag("h1").first().text().trim();
        power.name = name;
        Element aboutTable = document.selectFirst(".profile-table");
        Element tBody = aboutTable.getElementsByTag("tbody").first();
        Elements tRows = tBody.getElementsByTag("tr");
        for (Element row : tRows) {
            var columns = row.getElementsByTag("td");
            var label  = columns.get(0).text().trim();
            var text = columns.get(1).text().trim();

            switch (label) {
                case "Tier":
                    power.tier = text;
                    break;
                case "Score":
                    try {
                        power.score = Integer.valueOf(text);                        
                    } catch (NumberFormatException e) {
                        // ignore
                    }
                    break;
                case "Aliases":
                    power.aliases = text;
                    break;
            
                default:
                    power.description = text;
                    break;
            }
        }
        return power;
    }
}
