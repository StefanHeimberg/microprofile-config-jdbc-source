package com.example.myproject.config;

import org.flywaydb.core.Flyway;

import javax.annotation.Resource;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Dependent
public class MyConfigConnectionProducer {

    @Resource
    private DataSource dataSource;

    @Produces
    @MyConfigConnection
    @Dependent
    public Connection produceConnection() throws SQLException {
        final Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.migrate();

        final Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        connection.setReadOnly(true);

        return connection;
    }

}
