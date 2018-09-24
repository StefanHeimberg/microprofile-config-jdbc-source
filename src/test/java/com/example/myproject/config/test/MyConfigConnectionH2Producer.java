package com.example.myproject.config.test;

import com.example.myproject.config.MyConfigConnection;
import org.flywaydb.core.Flyway;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Dependent
@Alternative
public class MyConfigConnectionH2Producer {

    private static final String DB_DRIVER = "org.h2.Driver";
    private static final String DB_URL = "jdbc:h2:mem:myconfig-test;DB_CLOSE_DELAY=-1";

    @Produces
    @MyConfigConnection
    @Dependent
    @Alternative
    public Connection produceConnection() {
        try {
            DriverManager.registerDriver((Driver) Class.forName(DB_DRIVER).newInstance());

            migrateDatabase(DB_URL, DB_DRIVER);

            final Connection connection = DriverManager.getConnection(DB_URL);
            connection.setAutoCommit(false);
            connection.setReadOnly(true);

            return connection;
        } catch (ClassNotFoundException | SQLException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Error while create datasource", e);
        }
    }

    public void disposeConnection(@Disposes @MyConfigConnection Connection connection) throws SQLException {
        if(!connection.isClosed()) {
            connection.close();
        }
    }

    private void migrateDatabase(final String url, final String driver) {
        final Map<String, String> flywayConfig = new HashMap<>();
        flywayConfig.put("flyway.url", url);
        flywayConfig.put("flyway.driver", driver);

        final Flyway flyway = new Flyway();
        flyway.configure(flywayConfig);
        flyway.migrate();
    }

}
