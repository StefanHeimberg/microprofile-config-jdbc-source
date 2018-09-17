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
    public void test() {
        assertEquals("my-value-1", config.getMyKey1());
        assertEquals("my-value-2", config.getMyKey2());
    }

}
