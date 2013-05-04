package com.mosaic.jk.config;

import com.mosaic.jk.TestUtils;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

import static com.mosaic.jk.TestUtils.assertArrayEqualsIgnoreOrder;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 *
 */
public class ConfigLoaderTests_multipleSources {

    private ConfigLoader configLoader         = new ConfigLoader();
    private File zeroConfigProjectDir = TestUtils.examplesDir( "zeroConfig/multipleSourcesZeroConfig" );
    private Config       config               = configLoader.loadConfigFor( zeroConfigProjectDir );


    @Test
    public void defaultGroupId() {
        assertEquals( "com.mosaic", config.groupId );
    }

    @Test
    public void defaultProjectName() {
        assertEquals( "Multiple Sources Zero Config", config.projectName );
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
    public void expectMainClass() {
        ModuleConfig module = config.modules.get(0);

        assertArrayEquals( new String[] {"com.mosaic.server.Main"}, module.mainFQNs );
    }

    @Test
    public void expectSourceDirectory() {
        ModuleConfig module    = config.modules.get(0);
        File         sourceDir = new File(zeroConfigProjectDir, "src");
        File         clientDir = new File(sourceDir, "client");
        File         serverDir = new File(sourceDir, "server");

        assertArrayEqualsIgnoreOrder( new File[] {clientDir, serverDir}, module.sourceDirectories );
    }

    @Test
    public void expectTestSourceDirectories() {
        ModuleConfig module    = config.modules.get(0);
        File         testDir   = new File(zeroConfigProjectDir, "tests");
        File         clientDir = new File(testDir, "client");
        File         serverDir = new File(testDir, "server");

        assertArrayEqualsIgnoreOrder( new File[] {clientDir, serverDir}, module.testDirectories );
    }

    @Test
    public void expectSingleJar() {
        ModuleConfig module = config.modules.get(0);

        assertEquals( "JAR", module.packageType );
    }

    @Test
    public void expectDefaultTestDependenciesOnly() {
        ModuleConfig module = config.modules.get(0);

        Dependency junit   = new Dependency( "junit", "junit", "4.8.2" );
        Dependency mockito = new Dependency( "org.mockito", "mockito-all", "1.9.5" );


        assertEquals( Arrays.asList( junit, mockito ), module.dependencies );
    }

}
