package com.example.myproject.config;

import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import static org.junit.Assert.assertEquals;

@RunWith(CdiTestRunner.class)
@Dependent
public class MyConfigTest {

    @Inject
    private MyConfig config;

    @Test
    public void test_load_from_microprofile_config_properties() {
        assertEquals("my-value-1", config.getMyKey1());
    }

    @Test
    public void test_load_from_system_properties() {
        assertEquals("my-value-2", config.getMyKey2());
    }

    @Test
    public void test_test_load_from_custom_config_source() {
        assertEquals("my-value-3", config.getMyKey3());
    }

}
