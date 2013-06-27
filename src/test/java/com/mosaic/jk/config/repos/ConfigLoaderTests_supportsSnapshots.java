package com.mosaic.jk.config.repos;

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

import static org.junit.Assert.assertTrue;

/**
 *
 */
public class ConfigLoaderTests_supportsSnapshots extends BaseConfigTestCase {

    public ConfigLoaderTests_supportsSnapshots() {
        super("repos/supportSnapshots");
    }


    @Test
    public void supportsSnapshots_true() {
        assertTrue(config.supportsSnapshots);
    }

}
