package com.example.myproject.config;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Objects.isNull;

@ApplicationScoped
public class MyConfigPropertyProvider {

    private static final Logger LOG = Logger.getLogger(MyConfigConfigSource.class.getSimpleName());

    public static final String CONFIG_SCHEMA = "myconfig.schema";
    public static final String CONFIG_TABLE = "myconfig.table";
    public static final String CONFIG_NAME_COLUMN = "myconfig.name-column";
    public static final String CONFIG_VALUE_COLUMN = "myconfig.value-column";
    public static final String CONFIG_MAX_VALUES = "myconfig.max-values";

    public static MyConfigPropertyProvider getInstance() {
        try {
            final Instance<MyConfigPropertyProvider> parameterProviderInst = CDI.current().select(MyConfigPropertyProvider.class);
            if (parameterProviderInst.isUnsatisfied()) {
                return null;
            } else {
                return parameterProviderInst.get();
            }
        } catch(final Throwable t) {
            return null;
        }
    }

    private static String valueOrDefault(final Map<String, String> config, final String key, final String defaultValue) {
        // check if system property is set.
        final String systemProperty = System.getProperty(key, null);
        if(!isNull(systemProperty) && !systemProperty.trim().isEmpty()) {
            return systemProperty;
        }

        // check if configuration is not given or does not contains key
        if(isNull(config) || !config.containsKey(key)) {
            return defaultValue;
        }

        // check if value is not null or empty.
        final String value = config.get(key);
        if(isNull(value) || value.trim().isEmpty()) {
            return defaultValue;
        }

        return value;
    }

    @Inject
    @MyConfigConnection
    private Instance<Connection> connectionInst;

    public Map<String, String> getProperties() {
        final Map<String, String> properties = new HashMap<>();

        try(final Connection connection = connectionInst.get();
            final Statement stmt = connection.createStatement()) {

            final String nameColumn = valueOrDefault(null, CONFIG_NAME_COLUMN, "name");
            final String valueColumn = valueOrDefault(null, CONFIG_VALUE_COLUMN, "value");
            final String schema = valueOrDefault(null, CONFIG_SCHEMA, null);
            final String table = valueOrDefault(null, CONFIG_TABLE, "config");
            final Integer maxValue = Integer.valueOf(valueOrDefault(null, CONFIG_MAX_VALUES, "300"));

            final StringBuilder query = new StringBuilder();
            query.append("SELECT " + nameColumn + ", " + valueColumn + " FROM ");

            if(!isNull(schema) && !schema.isEmpty()) {
                query.append(schema + ".");
            }
            query.append(table);

            stmt.setMaxRows(maxValue + 1);
            final ResultSet rs = stmt.executeQuery(query.toString());
            while (rs.next()) {
                if(properties.size() > maxValue) {
                    LOG.log(Level.WARNING, "There are more than {0} supported properties in Database.", maxValue);
                    break;
                }

                final String key = rs.getString(nameColumn);
                final String value = rs.getString(valueColumn);

                if(MyConfigKey.isKnonwKey(key)) {
                    properties.put(key, value);
                }
            }
        } catch (final SQLException e) {
            LOG.log(Level.SEVERE, "SQL Exception while reading value from Database", e);
            throw new RuntimeException(e);
        }

        return properties;
    }

    public String getProperty(final String propertyName) {
        if(!MyConfigKey.isKnonwKey(propertyName)) {
            return null;
        }

        final String value;

        try(final Connection connection = connectionInst.get();
            final Statement stmt = connection.createStatement()) {

            final String nameColumn = valueOrDefault(null, CONFIG_NAME_COLUMN, "name");
            final String valueColumn = valueOrDefault(null, CONFIG_VALUE_COLUMN, "value");
            final String schema = valueOrDefault(null, CONFIG_SCHEMA, null);
            final String table = valueOrDefault(null, CONFIG_TABLE, "config");

            final StringBuilder query = new StringBuilder();
            query.append("SELECT " + valueColumn + " FROM ");

            if(!isNull(schema) && !schema.isEmpty()) {
                query.append(schema + ".");
            }
            query.append(table);
            query.append(" WHERE " + nameColumn + "='" + propertyName + "'");
            query.append(" ORDER BY " + nameColumn + " ASC");

            stmt.setMaxRows(2);
            final ResultSet rs = stmt.executeQuery(query.toString());

            if(rs.next()) {
                value = rs.getString(valueColumn);
                if(rs.next()) {
                    LOG.log(Level.WARNING, "There are more than 1 property for name {0} in Database. taking first ordered by {1}", new Object[]{propertyName, nameColumn});
                }
            } else {
                value = null;
            }
        } catch (final SQLException e) {
            LOG.log(Level.SEVERE, "SQL Exception while reading value from Database. propertyName: {0}", new Object[]{propertyName, e});
            throw new RuntimeException(e);
        }

        return value;
    }

}
