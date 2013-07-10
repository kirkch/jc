package com.mosaic.jk.config.zeroconfig;

import com.mosaic.jk.config.*;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 *
 */
public class ConfigLoaderTests_singleSourceAndTestDirectories extends BaseConfigTestCase {

    public ConfigLoaderTests_singleSourceAndTestDirectories() {
        super("zeroConfig/singleSourceAndTestDirectories");
    }


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
        assertEquals( "0.0.1."+System.getProperty("user.name")+"_1", config.versionFull);
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
