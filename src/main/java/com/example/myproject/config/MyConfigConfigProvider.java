package com.example.myproject.config;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MyConfigConfigProvider implements ConfigSource {

    public static final String NAME = "datasourceConfigProvider";

    private final Map<String, String> properties = new HashMap();

    public MyConfigConfigProvider() {
        properties.put(MyConfigKeys.MY_KEY_1, "my-value-1");
        properties.put(MyConfigKeys.MY_KEY_2, "my-value-2");
    }

    @Override
    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    @Override
    public String getValue(final String propertyName) {
        return properties.get(propertyName);
    }

    @Override
    public String getName() {
        return NAME;
    }

}
