package com.mosaic.jk.config;

import com.mosaic.jk.TestUtils;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class ConfigLoaderTest_defaultCases {

    private ConfigLoader configLoader = new ConfigLoader();


    @Test
    public void defaultGroupId() {
        File   zeroConfigProjectDir = TestUtils.examplesDir("zeroConfig");
        Config config               = configLoader.loadConfigFor( zeroConfigProjectDir );

        assertEquals( "com.s5", config.groupId );
    }

    @Test
    public void defaultProjectName() {
        File   zeroConfigProjectDir = TestUtils.examplesDir("zeroConfig");
        Config config               = configLoader.loadConfigFor( zeroConfigProjectDir );

        assertEquals( "Zero Config", config.projectName );
    }

    @Test
    public void defaultVersion() {
        File   zeroConfigProjectDir = TestUtils.examplesDir("zeroConfig");
        Config config               = configLoader.loadConfigFor( zeroConfigProjectDir );

        assertEquals( "0.0.1", config.version );
    }

    @Test
    public void defaultMain() {
        File   zeroConfigProjectDir = TestUtils.examplesDir("zeroConfig");
        Config config               = configLoader.loadConfigFor( zeroConfigProjectDir );

        assertEquals( "com.s5.zeroconfig.Main", config.mainFQN );
    }

}
