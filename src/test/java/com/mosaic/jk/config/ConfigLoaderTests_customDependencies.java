package com.mosaic.jk.config;

import com.mosaic.jk.TestUtils;
import com.mosaic.jk.env.Environment;
import com.mosaic.jk.env.EnvironmentFake;
import com.mosaic.jk.utils.Function1;
import com.mosaic.jk.utils.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.mosaic.jk.TestUtils.assertArrayEqualsIgnoreOrder;
import static org.junit.Assert.assertEquals;

/**
 *
 */
public class ConfigLoaderTests_customDependencies {

    private Environment env           = new EnvironmentFake();
    private ConfigLoader configLoader = new ConfigLoader(env);
    private File zeroConfigProjectDir = TestUtils.examplesDir("dependencies/multipleModulesAndDependencies");
    private Config       config       = configLoader.loadConfigFor( zeroConfigProjectDir );




    @Test
    public void expectTwoModules() {
        List<ModuleConfig> modules = config.modules;

        List<String> moduleNames = ListUtils.map( modules, new Function1<ModuleConfig,String>() {
            public String invoke(ModuleConfig m) {
                return m.moduleNameNbl;
            }
        });

        Collections.sort(moduleNames);

//        ModuleConfig module = modules.get(0);

//        Dependency junit   = new Dependency( DependencyScope.TEST, "junit", "junit", "4.8.2" );
//        Dependency mockito = new Dependency( DependencyScope.TEST, "org.mockito", "mockito-all", "1.9.5" );
//
//
        assertEquals( Arrays.asList("client","server"), moduleNames );
    }

    @Test
    public void checkClientDependencies() {
        ModuleConfig module = ListUtils.selectFirstMatch(config.modules, new Function1<ModuleConfig, Boolean>() {
            public Boolean invoke(ModuleConfig m) {
                return StringUtils.equals(m.moduleNameNbl, "client");
            }
        });

        Dependency rx          = new Dependency( DependencyScope.COMPILE, "com.netflix.rxjava", "rxjava-core", "0.8.4" );
        Dependency junit       = new Dependency( DependencyScope.TEST, "junit", "junit", "4.8.2" );
        Dependency reflections = new Dependency( DependencyScope.COMPILE, "org.reflections", "reflections", "0.9.9-RC1" );

        assertEquals( Arrays.asList(rx,junit,reflections), module.dependencies );
    }

    @Test
    public void checkServerDependencies() {
        ModuleConfig module = ListUtils.selectFirstMatch(config.modules, new Function1<ModuleConfig, Boolean>() {
            public Boolean invoke(ModuleConfig m) {
                return StringUtils.equals(m.moduleNameNbl, "server");
            }
        });

        Dependency rx     = new Dependency( DependencyScope.COMPILE, "com.netflix.rxjava", "rxjava-core", "0.8.4" );
        Dependency junit  = new Dependency( DependencyScope.TEST, "junit", "junit", "4.8.2" );
        Dependency client = new Dependency( DependencyScope.COMPILE, config.groupId, "client", config.versionNumber, null, true );
        Dependency netty  = new Dependency( DependencyScope.COMPILE, "io.netty", "netty-all", "4.0.0.CR2" );

        assertEquals( Arrays.asList(rx,junit,client,netty), module.dependencies );
    }

}
