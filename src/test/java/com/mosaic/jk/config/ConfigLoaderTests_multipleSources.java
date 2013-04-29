package com.mosaic.jk.config;

import com.mosaic.jk.TestUtils;

import java.io.File;

/**
 *
 */
public class ConfigLoaderTests_multipleSources {

    private ConfigLoader configLoader         = new ConfigLoader();
    private File         zeroConfigProjectDir = TestUtils.examplesDir( "zeroConfig/multipleSourcesZeroConfig" );

//    @Test
//    public void defaultGroupId() {
//        Config config = configLoader.loadConfigFor( zeroConfigProjectDir );
//
//        assertEquals( "com.mosaic", config.groupId );
//    }
//
//    @Test
//    public void defaultProjectName() {
//        Config config = configLoader.loadConfigFor( zeroConfigProjectDir );
//
//        assertEquals( "Multiple Sources Zero Config", config.projectName );
//    }
//
//    @Test
//    public void defaultVersion() {
//        Config config = configLoader.loadConfigFor( zeroConfigProjectDir );
//
//        assertEquals( "0.0.1", config.versionNumber );
//    }
//
//    @Test
//    public void defaultMain() {
//        Config config = configLoader.loadConfigFor( zeroConfigProjectDir );
//
//        assertEquals( "com.mosaic.server.Main", config.mainFQN );
//    }

}
