package org.acme;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

@ApplicationScoped
public class Output {

    private static final String REPO_ROOT = "https://github.com/cescoffier/supes-data/raw/master/characters";

    private static final String HERO_IMPORT_DDL = """
            -- new hero
            INSERT INTO hero(id, name, otherName, picture, level)
            VALUES (nextval('hero_id_seq'), '%s', '%s', '%s', %d);
            """;
    private static final String VILLAIN_IMPORT_DDL = """
            -- new villain
            INSERT INTO villain(id, name, otherName, picture, level)
            VALUES (nextval('villain_id_seq'), '%s', '%s', '%s', %d);
            """;
    private static final String POWER_IMPORT_DDL = """
            INSERT INTO power(id, name, description, aliases, score, tier) 
            VALUES(%d, '%s', '%s', '%s', %d, '%s');
            """;
    private static final String HERO_POWER_ASSOCIATION_IMPORT_DDL = """
            INSERT INTO hero_power(hero_id, power_id)
            VALUES(currval('hero_id_seq'), %d);
            """;
    private static final String VILLAIN_POWER_ASSOCIATION_IMPORT_DDL = """
            INSERT INTO villain_power(villain_id, power_id)
            VALUES(currval('villain_id_seq'), %d);
            """;
    private static final String ALTER_POWER_SEQUENCE_DDL = """
            ALTER SEQUENCE power_id_seq RESTART WITH %d;
            """;

    @Inject
    ObjectMapper mapper;

    @Inject
    Logger logger;

    public void write(List<Character> characters, Map<String, Power> powers) {
        StringBuilder importHeroes = new StringBuilder();
        StringBuilder importVillains = new StringBuilder();
        List<OutputCharacter> heroes = new ArrayList<>();
        List<OutputCharacter> villains = new ArrayList<>();
        AtomicInteger heroPowerIdSeq = new AtomicInteger(1);
        AtomicInteger villainPowerIdSeq = new AtomicInteger(1);
        for (Character character : characters) {
            if (character.isHero()) {
                importHeroes.append(
                    HERO_IMPORT_DDL.formatted(
                        character.name, character.otherName, REPO_ROOT + "/" + character.picture, character.level));
                
                character.powers.forEach(p -> {
                    //lookup power
                    logger.debugf("looking for [%s] in the Powers' map...", p);
                    var power = Optional.ofNullable(powers.get(p));
                    power.ifPresentOrElse(pp -> {
                        if (pp.id == 0) { //not persisted yet
                            pp.id = heroPowerIdSeq.getAndIncrement();
                            importHeroes.append(POWER_IMPORT_DDL.formatted(
                                pp.id, scapeSingleQuotes(pp.name), scapeSingleQuotes(pp.description), scapeSingleQuotes(pp.aliases), pp.score, scapeSingleQuotes(pp.tier)));
                        }
                        importHeroes.append(HERO_POWER_ASSOCIATION_IMPORT_DDL.formatted(pp.id));
                    }, () -> {
                        logger.debugf("\t>>> [%s] not found in the Powers' map!", p);
                    });
                });
                heroes.add(new OutputCharacter(character));
            } else { // Villain
                importVillains.append(
                    VILLAIN_IMPORT_DDL.formatted(
                        character.name, character.otherName, REPO_ROOT + "/" + character.picture, character.level));

                character.powers.forEach(p -> {
                    //lookup power
                    logger.debugf("looking for [%s] in the Powers' map...", p);
                    var power = Optional.ofNullable(powers.get(p));
                    power.ifPresentOrElse(pp -> {
                        if (pp.id == 0) { //not persisted yet
                            pp.id = villainPowerIdSeq.getAndIncrement();
                            importVillains.append(POWER_IMPORT_DDL.formatted(
                                pp.id, scapeSingleQuotes(pp.name), scapeSingleQuotes(pp.description), scapeSingleQuotes(pp.aliases), pp.score, scapeSingleQuotes(pp.tier)));
                        }
                        importVillains.append(VILLAIN_POWER_ASSOCIATION_IMPORT_DDL.formatted(pp.id));
                    }, () -> {
                        logger.debugf("\t>>> [%s] not found in the Powers' map!", p);
                    });
                });
                villains.add(new OutputCharacter(character));
            }
        }

        importHeroes.append(ALTER_POWER_SEQUENCE_DDL.formatted(heroPowerIdSeq.get()));
        importVillains.append(ALTER_POWER_SEQUENCE_DDL.formatted(villainPowerIdSeq.get()));
        writeImportFiles(importHeroes, importVillains);
        writeDBFiles(heroes, villains);

        logger.infof("Wrote %d heroes", heroes.size());
        logger.infof("Wrote %d villains", villains.size());
    }

    private String scapeSingleQuotes(String str) {
        return str != null ? str.replace("'", "''") : null;
    }

    private void writeImportFiles(StringBuilder importHeroes, StringBuilder importVillains) {
        File heroes = new File("../heroes-import.sql");
        File villains = new File("../villains-import.sql");
        try {
            Files.writeString(heroes.toPath(), importHeroes.toString(), StandardCharsets.UTF_8);
            Files.writeString(villains.toPath(), importVillains.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void writeDBFiles(List<OutputCharacter> importHeroes, List<OutputCharacter> importVillains) {
        File heroes = new File("../heroes.json");
        File villains = new File("../villains.json");
        try {
            Files.writeString(heroes.toPath(), mapper.writeValueAsString(importHeroes), StandardCharsets.UTF_8);
            Files.writeString(villains.toPath(), mapper.writeValueAsString(importVillains), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static class OutputCharacter {
        public String name;
        public String otherName;
        public int level;
        public String picture;
        public List<String> powers;

        public OutputCharacter(Character character) {
            this.name = character.name;
            this.otherName = character.otherName;
            this.level = character.level;
            this.picture = REPO_ROOT + "/" + character.picture;
            this.powers = character.powers;
        }
    }

}
