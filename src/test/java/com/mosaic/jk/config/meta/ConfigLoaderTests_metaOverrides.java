package com.mosaic.jk.config.meta;

import com.mosaic.jk.TestUtils;
import com.mosaic.jk.config.BaseConfigTestCase;
import com.mosaic.jk.config.Config;
import com.mosaic.jk.config.ConfigLoader;
import com.mosaic.jk.env.Environment;
import com.mosaic.jk.env.EnvironmentFake;
import com.mosaic.jk.env.EnvironmentImpl;
import com.mosaic.jk.io.ProjectWorkspaceImpl;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class ConfigLoaderTests_metaOverrides extends BaseConfigTestCase {

    public ConfigLoaderTests_metaOverrides() {
        super("meta/all");
    }

    @Test
    public void defaultJavaVersion() {
        assertEquals( "1.7", config.javaVersion );
    }

}
