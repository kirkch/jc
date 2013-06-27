package com.mosaic.jk.config.dependencies;

import com.mosaic.jk.TestUtils;
import com.mosaic.jk.config.*;
import com.mosaic.jk.env.Environment;
import com.mosaic.jk.env.EnvironmentFake;
import com.mosaic.jk.io.ProjectWorkspaceImpl;
import org.junit.Test;

import java.io.File;
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
