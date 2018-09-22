package com.example.myproject.config;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class MyConfig {

    @Inject
    private Config config;

    @Inject
    @ConfigProperty(name = MyConfigKeys.MY_KEY_1)
    private String configValue1;

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

}
