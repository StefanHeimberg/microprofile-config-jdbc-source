package com.example.myproject.config;

import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.eclipse.microprofile.config.Config;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import static junit.framework.TestCase.assertNotNull;

@RunWith(CdiTestRunner.class)
@Dependent
public class MyConfigConfigProviderTest {

    @Inject
    private Config config;

    @Test
    public void test_config_injection() {
        assertNotNull(config);
    }

}
