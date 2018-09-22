package com.example.myproject.config.test;

import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.flywaydb.core.Flyway;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;

import java.util.HashMap;
import java.util.Map;

import static com.example.myproject.config.MyConfigConfigSource.CONFIG_DB_DRIVER;
import static com.example.myproject.config.MyConfigConfigSource.CONFIG_DB_URL;
import static java.util.Objects.isNull;

public class MyConfigH2CdiTestRunner extends CdiTestRunner {

    public MyConfigH2CdiTestRunner(final Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    public void run(RunNotifier runNotifier) {
        final String oldUrl = System.getProperty(CONFIG_DB_URL);
        final String oldDriver = System.getProperty(CONFIG_DB_DRIVER);
        try {
            final String url = "jdbc:h2:mem:myconfig;DB_CLOSE_DELAY=-1";
            final String driver = "org.h2.Driver";

            System.setProperty(CONFIG_DB_URL, url);
            System.setProperty(CONFIG_DB_DRIVER, driver);

            migrateDatabase(url, driver);
            super.run(runNotifier);
        } finally {
            if(!isNull(oldUrl)) {
                System.setProperty(CONFIG_DB_URL, oldUrl);
            } else {
                System.clearProperty(CONFIG_DB_URL);
            }
            if(!isNull(oldDriver)) {
                System.setProperty(CONFIG_DB_DRIVER, oldDriver);
            } else {
                System.clearProperty(CONFIG_DB_DRIVER);
            }
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
