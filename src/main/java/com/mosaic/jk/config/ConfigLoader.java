package com.mosaic.jk.config;

import com.mosaic.jk.io.ProjectWorkspace;
import com.mosaic.jk.io.ProjectWorkspaceImpl;
import com.mosaic.jk.utils.ListUtils;
import com.mosaic.jk.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class ConfigLoader {

    public Config loadConfigFor( File projectDirectory ) {
        ProjectWorkspace fs = new ProjectWorkspaceImpl( projectDirectory );

        Config config = new Config();

        config.projectName = fetchDefaultProjectName( projectDirectory );
        config.groupId     = fetchDefaultGroupId( projectDirectory );
        config.versionNumber = fetchDefaultVersionNumber();

        config.modules       = fetchDefaultModules( fs );

        return config;
    }

    private String fetchDefaultVersionNumber() {
        return "0.0.1";
    }

    private String fetchDefaultProjectName( File projectDirectory ) {
        String dirName = projectDirectory.getName();

        List<String> tokens = StringUtils.tokeniseCamelCasedString( dirName );
        return ListUtils.concat( tokens, " " );
    }

    private static final List<String> STANDARD_OPENING_PACKAGES = Arrays.asList( "com", "net", "org" );

    private String fetchDefaultGroupId( File projectDirectory ) {
        File sourceDirectory = fetchSourceDirectory( projectDirectory );

        File[] children1 = sourceDirectory.listFiles();

        if ( children1 == null || children1.length == 0 ) {
            return "sandbox";
        }

        File selectedChild1 = children1[0];
        File[] children2 = selectedChild1.listFiles();

        if ( children2 == null || children2.length == 0 ) {
            return selectedChild1.getName();
        }

        File selectedChild2 = children2[0];

        return selectedChild1.getName() + "." + selectedChild2.getName();
    }

    private List<ModuleConfig> fetchDefaultModules( ProjectWorkspace project ) {
        List<ModuleConfig> modules = new ArrayList<ModuleConfig>();

        ModuleConfig module = new ModuleConfig();

        module.mainFQNs          = fetchDefaultMainFQN(project);
        module.sourceDirectories = fetchDefaultSourceDirectories( project );
        module.testDirectories   = fetchDefaultTestDirectories( project );
        module.packageAs         = "JAR";
        module.dependencies      = fetchDefaultDependencies();
        modules.add( module );

        return modules;
    }

    private List<Dependency> fetchDefaultDependencies() {
        Dependency junit   = Dependency.test("junit","junit","4.8.2");
        Dependency mockito = Dependency.test("org.mockito","mockito-all","1.9.5");

        return Arrays.asList(junit,mockito);
    }

    private File[] fetchDefaultTestDirectories( ProjectWorkspace project ) {
        return project.scanForTestDirectories();
    }

    private File[] fetchDefaultSourceDirectories( ProjectWorkspace project ) {
        return project.scanForSourceDirectories();
    }


    private String[] fetchDefaultMainFQN( ProjectWorkspace project ) {
        return project.scanForMainClassFQNs();
    }



    private File fetchSourceDirectory( File projectDirectory ) {
        return new File( projectDirectory, "src" );
    }

}
