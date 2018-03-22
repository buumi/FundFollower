package com.buhmen;

import java.util.HashMap;
import java.util.Map;

public class DBData {
    private String name;
    private Map<String, Double> values;

    public DBData(String name) {
        this.name = name;
        values = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public Map<String, Double> getValues() {
        return values;
    }
}
