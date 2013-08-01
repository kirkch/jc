package com.mosaic.jc.config.meta;

import com.mosaic.jc.config.BaseConfigTestCase;
import org.junit.Test;

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
