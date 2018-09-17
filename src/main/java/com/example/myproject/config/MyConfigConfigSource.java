package com.example.myproject.config;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MyConfigConfigSource implements ConfigSource {

    public static final String NAME = "datasourceConfigProvider";

    private final Map<String, String> properties = new HashMap();

    void reloadConfiguration(final Connection connection) {
        try (final Statement stmt = connection.createStatement()) {

            final ResultSet rs = stmt.executeQuery("SELECT key,value FROM t_config");

            properties.clear();
            while (rs.next()) {
                final String key = rs.getString("key");
                final String value = rs.getString("value");
                properties.put(key, value);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getOrdinal() {
        return 120;
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
