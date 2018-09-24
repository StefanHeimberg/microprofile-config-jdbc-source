package com.example.myproject.config;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.util.Map;

import static java.util.Objects.isNull;

public class MyConfigConfigSource implements ConfigSource {

    public static final String NAME = "myConfigConfigSource";

    private MyConfigPropertyProvider parameterProvider;

    public boolean initialize() {
        if(isNull(parameterProvider)) {
            parameterProvider = MyConfigPropertyProvider.getInstance();
        }
        return !isNull(parameterProvider);
    }

    @Override
    public int getOrdinal() {
        return 120;
    }

    @Override
    public Map<String, String> getProperties() {
        if(initialize()) {
            return parameterProvider.getProperties();
        } else {
            return null;
        }

    }

    @Override
    public String getValue(final String propertyName) {
        if(initialize()) {
            return parameterProvider.getProperty(propertyName);
        } else {
            return null;
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

}
