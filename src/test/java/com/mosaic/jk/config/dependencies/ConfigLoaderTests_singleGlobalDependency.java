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
public class ConfigLoaderTests_singleGlobalDependency {
    private Environment  env                  = new EnvironmentFake();
    private ConfigLoader configLoader         = new ConfigLoader(env);
    private File         zeroConfigProjectDir = TestUtils.examplesDir( "dependencies/globalMavenDependency" );
    private Config config               = configLoader.loadConfigFor( new ProjectWorkspaceImpl(zeroConfigProjectDir) );


    @Test
    public void defaultGroupId() {
        assertEquals( "com.s5", config.groupId );

        assertEquals( 1, config.modules.size() );

        ModuleConfig module = config.modules.get(0);

        assertEquals( null, module.moduleNameNbl );

        assertEquals( Arrays.asList(new Dependency(DependencyScope.COMPILE, "org.apache.commons", "commons-lang3", "3.0.1")), module.dependencies );
    }

}
