package com.mosaic.jc.config;

import com.mosaic.jc.config.Config;
import com.mosaic.jc.config.ConfigLoader;
import com.mosaic.jc.TestUtils;
import com.mosaic.jc.env.Environment;
import com.mosaic.jc.env.EnvironmentImpl;

import java.io.File;

/**
 *
 */
public abstract class BaseConfigTestCase {

    protected final File         zeroConfigProjectDir;
    protected final Environment  env;
    protected final ConfigLoader configLoader;
    protected final Config config;


    protected BaseConfigTestCase( String relativeConfigPath ) {
        zeroConfigProjectDir = TestUtils.examplesDir(relativeConfigPath);
        env                  = new EnvironmentImpl(zeroConfigProjectDir);
        configLoader         = new ConfigLoader(env);
        config               = configLoader.loadConfig();
    }

}
