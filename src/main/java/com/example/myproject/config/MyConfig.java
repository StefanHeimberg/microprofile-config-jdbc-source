package com.example.myproject.config;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.sql.Connection;

@ApplicationScoped
public class MyConfig {

    @Inject
    private Config config;

    @Inject
    @ConfigProperty(name = MyConfigKeys.MY_KEY_1)
    private String configValue1;

    @Inject
    private EntityManager em;

    @PostConstruct
    public void init() {
        reload();
    }

    public String getMyKey1() {
        return configValue1;
    }

    public String getMyKey2() {
        return config.getValue(MyConfigKeys.MY_KEY_2, String.class);
    }

    public String getMyKey3() {
        return config.getValue(MyConfigKeys.MY_KEY_3, String.class);
    }

    public Config getConfig() {
        return config;
    }

    public void reload() {
        config.getConfigSources().forEach(cs -> {
            if(MyConfigConfigSource.NAME.equals(cs.getName())) {
                final MyConfigConfigSource myConfigConfigSource = (MyConfigConfigSource) cs;
                myConfigConfigSource.reloadConfiguration(em.unwrap(Connection.class));
            }
        });
    }

}
