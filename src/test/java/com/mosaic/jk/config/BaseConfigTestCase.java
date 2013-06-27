package com.mosaic.jk.config;

import com.mosaic.jk.TestUtils;
import com.mosaic.jk.env.Environment;
import com.mosaic.jk.env.EnvironmentImpl;

import java.io.File;

/**
 *
 */
public abstract class BaseConfigTestCase {

    protected final File         zeroConfigProjectDir;
    protected final Environment  env;
    protected final ConfigLoader configLoader;
    protected final Config       config;


    protected BaseConfigTestCase( String relativeConfigPath ) {
        zeroConfigProjectDir = TestUtils.examplesDir(relativeConfigPath);
        env                  = new EnvironmentImpl(zeroConfigProjectDir);
        configLoader         = new ConfigLoader(env);
        config               = configLoader.loadConfig();
    }

}
