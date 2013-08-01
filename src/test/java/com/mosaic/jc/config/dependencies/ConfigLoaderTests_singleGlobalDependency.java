package com.mosaic.jc.config.dependencies;

import com.mosaic.jc.config.BaseConfigTestCase;
import com.mosaic.jc.config.Dependency;
import com.mosaic.jc.config.DependencyScope;
import com.mosaic.jc.config.ModuleConfig;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class ConfigLoaderTests_singleGlobalDependency extends BaseConfigTestCase {

    public ConfigLoaderTests_singleGlobalDependency() {
        super("dependencies/globalMavenDependency");
    }


    @Test
    public void defaultGroupId() {
        assertEquals( "com.s5", config.groupId );

        assertEquals( 1, config.modules.size() );

        ModuleConfig module = config.modules.get(0);

        assertEquals( null, module.moduleNameNbl );

        assertEquals( Arrays.asList(new Dependency(DependencyScope.COMPILE, "org.apache.commons", "commons-lang3", "3.0.1")), module.dependencies );
    }

}
