package com.example.myproject.health;

import com.example.myproject.config.MyConfig;
import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Health
@ApplicationScoped
public class MyprojectServicePingCheck implements HealthCheck {

    @Inject
    private MyConfig config;

    @Override
    public HealthCheckResponse call() {
        final HealthCheckResponseBuilder builder = HealthCheckResponse.named("myproject-service-ping-check");

        if("my-value-1".equals(config.getMyKey1())
                && ("my-value-2".equals(config.getMyKey2()))
                && ("my-value-3".equals(config.getMyKey3()))) {
            return builder.up()
                    .withData("pong", 1)
                    .withData("timestamp", System.currentTimeMillis())
                    .build();
        } else {
            return builder.down().build();
        }

    }

}
