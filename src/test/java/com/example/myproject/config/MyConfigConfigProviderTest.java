package com.example.myproject.config;

import com.example.myproject.config.test.MyConfigH2CdiTestRunner;
import org.eclipse.microprofile.config.Config;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import static junit.framework.TestCase.assertNotNull;

@RunWith(MyConfigH2CdiTestRunner.class)
@Dependent
public class MyConfigConfigProviderTest {

    @Inject
    private Config config;

    @Test
    public void test_config_injection() {
        assertNotNull(config);
    }

}
