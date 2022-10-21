package org.acme;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@ApplicationScoped
public class PowerDb {

    @Inject
    ObjectMapper mapper;

    @Inject
    Logger logger;

    private Map<String, Power> powers = new HashMap<>();

    public void load() {
        File file = new File(Constants.POWERS, File.separatorChar + "super-powers.json");
        List<Power> powersList = new ArrayList<>();
        if (!file.exists()) {
            throw new IllegalStateException("Unable to read files inside " + Constants.POWERS.getAbsolutePath());
        }

        try {
            powersList = mapper.readValue(file, new TypeReference<List<Power>>() { });
            this.powers = powersList.stream()
                .collect(Collectors.toMap(Power::getName, Function.identity()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    public Map<String, Power> map() {
        return powers;
    }

}
