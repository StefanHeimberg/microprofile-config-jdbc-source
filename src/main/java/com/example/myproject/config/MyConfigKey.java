package com.example.myproject.config;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class MyConfigKey {

    private static final Map<String, String> keys;

    static {
        keys = Arrays.asList(MyConfigKey.class.getDeclaredFields()).stream()
                .filter(f -> Modifier.isStatic(f.getModifiers()))
                .filter(f -> Modifier.isFinal(f.getModifiers()))
                .filter(f -> String.class.equals(f.getType()))
                .collect(Collectors.toMap(Field::getName, f -> {
                    try {
                        return (String)f.get(null);
                    } catch (final IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }));
    }

    public static boolean isKnonwKey(final String key) {
        final boolean isKnonwKey = keys.containsValue(key);
        return isKnonwKey;
    }

    public static final String MY_KEY_1 = "my.config.key.1";
    public static final String MY_KEY_2 = "my.config.key.2";
    public static final String MY_KEY_3 = "my.config.key.3";

    private MyConfigKey() {
        // cannot be instantiated
    }

}
