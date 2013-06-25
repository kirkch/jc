package com.mosaic.jk.config.repos;

import com.mosaic.jk.TestUtils;
import com.mosaic.jk.config.Config;
import com.mosaic.jk.config.ConfigLoader;
import com.mosaic.jk.env.Environment;
import com.mosaic.jk.env.EnvironmentFake;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;

/**
 *
 */
public class ConfigLoaderTests_supportsSnapshots {

    private Environment  env          = new EnvironmentFake();
    private ConfigLoader configLoader = new ConfigLoader(env);
    private File         projectDir   = TestUtils.examplesDir("repos/supportSnapshots");
    private Config       config       = configLoader.loadConfigFor(projectDir);


    @Test
    public void supportsSnapshots_true() {
        assertTrue(config.supportsSnapshots);
    }

}
