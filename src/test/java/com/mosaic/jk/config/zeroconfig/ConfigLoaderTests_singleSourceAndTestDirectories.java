package com.mosaic.jk.config.zeroconfig;

import com.mosaic.jk.TestUtils;
import com.mosaic.jk.config.*;
import com.mosaic.jk.env.Environment;
import com.mosaic.jk.env.EnvironmentFake;
import com.mosaic.jk.io.ProjectWorkspaceImpl;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 *
 */
public class ConfigLoaderTests_singleSourceAndTestDirectories {

    private Environment  env                  = new EnvironmentFake();
    private ConfigLoader configLoader         = new ConfigLoader(env);
    private File         zeroConfigProjectDir = TestUtils.examplesDir( "zeroConfig/singleSourceAndTestDirectories" );
    private Config       config               = configLoader.loadConfigFor( new ProjectWorkspaceImpl(zeroConfigProjectDir) );


    @Test
    public void defaultGroupId() {
        assertEquals( "com.s5", config.groupId );
    }

    @Test
    public void defaultProjectName() {
        assertEquals( "Single Source And Test Directories", config.projectName );
    }

    @Test
    public void defaultVersion() {
        assertEquals( "0.0.1", config.versionNumber );
    }

    @Test
    public void expectOnlyOneModule() {
        assertEquals( 1, config.modules.size() );
    }

    @Test
    public void expectImplicitModule_thatIsNoModuleName() {
        ModuleConfig module = config.modules.get(0);

        assertNull( module.moduleNameNbl );
    }

    @Test
    public void expectNoMainClass() {
        ModuleConfig module = config.modules.get(0);

        assertArrayEquals( new String[] {}, module.mainFQNs );
    }

    @Test
    public void expectSourceDirectory() {
        ModuleConfig module    = config.modules.get(0);
        File         sourceDir = new File(zeroConfigProjectDir, "src");

        assertArrayEquals( new File[] {sourceDir}, module.sourceDirectories );
    }

    @Test
    public void expectTestSourceDirectories() {
        ModuleConfig module  = config.modules.get(0);
        File         testDir = new File(zeroConfigProjectDir, "tests");

        assertArrayEquals( new File[] {testDir}, module.testDirectories );
    }

    @Test
    public void expectSingleJar() {
        ModuleConfig module = config.modules.get(0);

        assertEquals( "JAR", module.packageType );
    }

    @Test
    public void expectDefaultTestDependenciesOnly() {
        ModuleConfig module = config.modules.get(0);

        Dependency junit   = new Dependency( DependencyScope.TEST, "junit", "junit", "4.8.2" );
        Dependency mockito = new Dependency( DependencyScope.TEST, "org.mockito", "mockito-all", "1.9.5" );


        assertEquals( Arrays.asList( junit, mockito ), module.dependencies );
    }

}
