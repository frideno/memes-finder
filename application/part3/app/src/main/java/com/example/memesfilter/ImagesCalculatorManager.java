package com.example.memesfilter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

public class ImagesCalculatorManager {
    private static ImagesCalculatorManager instance;

    private ConcurrentHashMap<String, ImagesCalculator> calculators;

    private ImagesCalculatorManager() {
        calculators = new ConcurrentHashMap<>();
    }

    public static ImagesCalculatorManager getInstance() {
        if (instance == null) {
            instance = new ImagesCalculatorManager();
        }

        return instance;
    }

    public String addCalculator(ImagesCalculator imagesCalculator) {
        String key = UUID.randomUUID().toString();
        calculators.put(key, imagesCalculator);
        return key;
    }

    public ImagesCalculator getCalculator(String key) {
        if (calculators.containsKey(key)) {
            return calculators.get(key);

        }
        return null;
        // TODO: add exception.X
    }

}
