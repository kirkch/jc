package com.mosaic.jk.config.zeroconfig;

import com.mosaic.jk.config.*;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Given zero config in a project with a single implicit/unnamed module.
 */
public class ConfigLoaderTests_defaultCases extends BaseConfigTestCase {

    public ConfigLoaderTests_defaultCases() {
        super("zeroConfig/zeroConfig");
    }


    @Test
    public void defaultGroupId() {
        assertEquals( "com.s5", config.groupId );
    }

    @Test
    public void defaultProjectName() {
        assertEquals( "Zero Config", config.projectName );
    }

    @Test
    public void defaultVersionFull() {
        assertEquals( "0.0.1."+System.getProperty("user.name")+"_1", config.versionFull);
    }

    @Test
    public void defaultVersion() {
        assertEquals( "0.0.1", config.version );
    }

    @Test
    public void defaultJavaVersion() {
        assertEquals( "1.6", config.javaVersion );
    }

    @Test
    public void expectDestinationDirectory() {
        assertEquals( new File(config.rootDirectory, "target/classes"), config.destinationDirectory );
    }

    @Test
    public void expectOnlyOneModule() {
        assertEquals( 1, config.modules.size() );
    }

    @Test
    public void expectImplicitModule_thatIsNoModuleName() {
        ModuleConfig module = config.modules.get(0);

        assertNull(module.moduleNameNbl);
    }

    @Test
    public void expectOneMainClass() {
        ModuleConfig module = config.modules.get(0);

        assertArrayEquals(new String[]{"com.s5.zeroconfig.Main"}, module.mainFQNs);
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

        assertEquals("JAR", module.packageType);
    }

    @Test
    public void expectNoRepositoryURLs() {
        assertEquals(Collections.EMPTY_LIST, config.downloadRepositories );
    }

    @Test
    public void expectSupportsSnapshots_false() {
        assertFalse(config.supportsSnapshots);
    }

    @Test
    public void expectDefaultTestDependenciesOnly() {
        ModuleConfig module = config.modules.get(0);

        Dependency junit   = new Dependency( DependencyScope.TEST, "junit", "junit", "4.8.2" );
        Dependency mockito = new Dependency( DependencyScope.TEST, "org.mockito", "mockito-all", "1.9.5" );


        assertEquals( Arrays.asList(junit,mockito), module.dependencies );
    }

}
