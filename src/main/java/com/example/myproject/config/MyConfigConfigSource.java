package com.example.myproject.config;

import org.eclipse.microprofile.config.spi.ConfigSource;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Objects.isNull;

public class MyConfigConfigSource implements ConfigSource {

    private static final Logger LOG = Logger.getLogger(MyConfigConfigSource.class.getSimpleName());

    public static final String NAME = "myConfigConfigSource";
    public static final String CONFIG_JNDI_NAME = "myconfig.jndi-name";
    public static final String CONFIG_DB_URL = "myconfig.jdbc-connection-url";
    public static final String CONFIG_DB_DRIVER = "myconfig.jdbc-connection-driver";
    public static final String CONFIG_DB_USERNAME = "myconfig.jdbc-connection-username";
    public static final String CONFIG_DB_PASSWORD = "myconfig.jdbc-connection-password";
    public static final String CONFIG_SCHEMA = "myconfig.schema";
    public static final String CONFIG_TABLE = "myconfig.table";
    public static final String CONFIG_NAME_COLUMN = "myconfig.name-column";
    public static final String CONFIG_VALUE_COLUMN = "myconfig.value-column";
    public static final String CONFIG_MAX_VALUES = "myconfig.max-values";

    private final Map<String, String> config;

    public MyConfigConfigSource() {
        this(null);
    }

    public MyConfigConfigSource(final Map<String, String> config) {
        this.config = config;
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

    private Connection getConnection() {
        final String dbDriver = valueOrDefault(config, CONFIG_DB_DRIVER, null);
        final String dbUrl = valueOrDefault(config, CONFIG_DB_URL, null);
        final String dbUsername = valueOrDefault(config, CONFIG_DB_USERNAME, null);
        final String dbPassword = valueOrDefault(config, CONFIG_DB_PASSWORD, null);

        if(!isNull(dbDriver) && !dbDriver.isEmpty()
                && !isNull(dbUrl) && !dbUrl.isEmpty()) {
            // Create new JDBC Connection
            try {
                DriverManager.registerDriver((Driver) Class.forName(dbDriver).newInstance());
                return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            } catch (ClassNotFoundException | SQLException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("Error while create datasource", e);
            }
        } else {
            // Lookup using JNDI
            try {
                final String jndiName = valueOrDefault(config, CONFIG_JNDI_NAME, "java:comp/DefaultDataSource");;
                final DataSource dataSource = (DataSource) new InitialContext().lookup(jndiName);
                return dataSource.getConnection();
            } catch (final NamingException | SQLException e) {
                throw new RuntimeException("Error while lookup datasource", e);
            }
        }
    }

    @Override
    public int getOrdinal() {
        return 120;
    }

    @Override
    public Map<String, String> getProperties() {
        final Map<String, String> properties = new HashMap<>();

        try(final Connection connection = getConnection();
            final Statement stmt = connection.createStatement()) {

            final String nameColumn = valueOrDefault(config, CONFIG_NAME_COLUMN, "name");
            final String valueColumn = valueOrDefault(config, CONFIG_VALUE_COLUMN, "value");
            final String schema = valueOrDefault(config, CONFIG_SCHEMA, null);
            final String table = valueOrDefault(config, CONFIG_TABLE, "config");
            final Integer maxValue = Integer.valueOf(valueOrDefault(config, CONFIG_MAX_VALUES, "300"));

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
                properties.put(key, value);
            }
        } catch (final SQLException e) {
            LOG.log(Level.SEVERE, "SQL Exception while reading value from Database", e);
            throw new RuntimeException(e);
        }

        return properties;
    }

    @Override
    public String getValue(final String propertyName) {
        final String value;

        try(final Connection connection = getConnection();
            final Statement stmt = connection.createStatement()) {

            final String nameColumn = valueOrDefault(config, CONFIG_NAME_COLUMN, "name");
            final String valueColumn = valueOrDefault(config, CONFIG_VALUE_COLUMN, "value");
            final String schema = valueOrDefault(config, CONFIG_SCHEMA, null);
            final String table = valueOrDefault(config, CONFIG_TABLE, "config");

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

    @Override
    public String getName() {
        return NAME;
    }

}
