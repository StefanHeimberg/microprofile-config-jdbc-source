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
    @ConfigProperty(name = MyConfigKeys.MY_KEY_2)
    private String configValue2;

    public String getMyKey1() {
        return config.getValue(MyConfigKeys.MY_KEY_1, String.class);
    }

    public String getMyKey2() {
        return configValue2;
    }

}
