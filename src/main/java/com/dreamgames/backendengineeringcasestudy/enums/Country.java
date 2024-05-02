package com.dreamgames.backendengineeringcasestudy.enums;


import java.util.List;
import java.util.Random;

public enum Country {
    TURKEY,
    UNITED_STATES,
    UNITED_KINGDOM,
    FRANCE,
    GERMANY;

    private static final List<Country> VALUES = List.of(values());
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    public static String assignRandomCountry()  {
        return VALUES.get(RANDOM.nextInt(SIZE)).name();
    }
}