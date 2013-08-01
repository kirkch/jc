package com.mosaic.jc.config.repos;

import com.mosaic.jc.config.BaseConfigTestCase;
import org.junit.Test;

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
