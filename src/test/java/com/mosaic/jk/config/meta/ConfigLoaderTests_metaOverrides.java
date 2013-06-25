package com.mosaic.jk.config.meta;

import com.mosaic.jk.TestUtils;
import com.mosaic.jk.config.Config;
import com.mosaic.jk.config.ConfigLoader;
import com.mosaic.jk.env.Environment;
import com.mosaic.jk.env.EnvironmentFake;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class ConfigLoaderTests_metaOverrides {

    private Environment env                  = new EnvironmentFake();
    private ConfigLoader configLoader         = new ConfigLoader(env);
    private File zeroConfigProjectDir = TestUtils.examplesDir("meta/all");
    private Config config               = configLoader.loadConfigFor( zeroConfigProjectDir );


    @Test
    public void defaultJavaVersion() {
        assertEquals( "1.7", config.javaVersion );
    }

}
