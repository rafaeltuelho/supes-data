package org.acme;

import java.util.List;

import javax.inject.Inject;

import org.jboss.logging.Logger;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "scrape", mixinStandardHelpOptions = true)
public class ScrapeCommand implements Runnable {

    @CommandLine.Option(names = "--scrape", required = false)
    boolean scrape;

    @CommandLine.Option(names = "--characters", required = false)
    boolean characters;

    @CommandLine.Option(names = "--powers", required = false)
    boolean powers;

    @CommandLine.Option(names = "--generate", required = false)
    boolean generate;

    final int MAX_PAGE = 120;

    @Inject
    Logger logger;

    @Inject
    JsoupHelper helper;

    @Inject
    CharacterList characterList;

    @Inject Scraper scraper;

    @Inject CharacterDb db;
    @Inject PowerDb powerDb;

    @Inject Output output;

    @Override
    public void run() {
        List<CharacterList.Entry> entries = characterList.entries();
        if (scrape && characters) {
            scraper.scrapeAll(entries);
        }

        if (scrape && powers) {
            scraper.scrapeSuperPowers();
        }

        if (generate) {
            db.load();
            powerDb.load();
            output.write(db.list(), powerDb.map());
        }

    //    var url = "/brother-voodoo/10-39/";
    //    var name = "Brother Voodoo";
    //    logger.infof("%s", scraper.scrape(new CharacterList.Entry(name, url)));
    }


}
