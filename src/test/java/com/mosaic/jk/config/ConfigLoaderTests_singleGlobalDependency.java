package com.mosaic.jk.config;

import com.mosaic.jk.TestUtils;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class ConfigLoaderTests_singleGlobalDependency {
    private ConfigLoader configLoader         = new ConfigLoader();
    private File         zeroConfigProjectDir = TestUtils.examplesDir( "dependencies/globalMavenDependency" );
    private Config       config               = configLoader.loadConfigFor( zeroConfigProjectDir );


    @Test
    public void defaultGroupId() {
        assertEquals( "com.s5", config.groupId );

        assertEquals( 1, config.modules.size() );

        ModuleConfig module = config.modules.get(0);

        assertEquals( null, module.moduleNameNbl );

        assertEquals( Arrays.asList(new Dependency("commons-lang", "commons-lang", "2.6")), module.dependencies );
    }

}
