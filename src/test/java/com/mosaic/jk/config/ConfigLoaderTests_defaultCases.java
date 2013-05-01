package com.mosaic.jk.config;

import com.mosaic.jk.TestUtils;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Given zero config in a project with a single implicit/unnamed module.
 */
public class ConfigLoaderTests_defaultCases {

    private ConfigLoader configLoader         = new ConfigLoader();
    private File         zeroConfigProjectDir = TestUtils.examplesDir("zeroConfig/zeroConfig");
    private Config       config               = configLoader.loadConfigFor( zeroConfigProjectDir );


    @Test
    public void defaultGroupId() {
        assertEquals( "com.s5", config.groupId );
    }

    @Test
    public void defaultProjectName() {
        assertEquals( "Zero Config", config.projectName );
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
    public void expectOneMainClass() {
        ModuleConfig module = config.modules.get(0);

        assertArrayEquals( new String[] {"com.s5.zeroconfig.Main"}, module.mainFQNs );
    }

    @Test
    public void expectSourceDirectory() {
        ModuleConfig module    = config.modules.get(0);
        File         sourceDir = new File(zeroConfigProjectDir, "src");

        assertArrayEquals( new File[] {sourceDir}, module.sourceDirectories );
    }

    @Test
    public void expectNoTestSourceDirectories() {
        ModuleConfig module = config.modules.get(0);

        assertArrayEquals( new File[] {}, module.testDirectories );
    }

    @Test
    public void expectSingleJar() {
        ModuleConfig module = config.modules.get(0);

        assertEquals( "JAR", module.packageType );
    }

    @Test
    public void expectDefaultTestDependenciesOnly() {
        ModuleConfig module = config.modules.get(0);

        Dependency junit   = Dependency.test("junit","junit","4.8.2");
        Dependency mockito = Dependency.test( "org.mockito", "mockito-all", "1.9.5" );


        assertEquals( Arrays.asList(junit,mockito), module.dependencies );
    }

}
