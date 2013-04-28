package com.mosaic.jk.config;

import com.mosaic.jk.io.ProjectWorkspace;
import com.mosaic.jk.io.ProjectWorkspaceImpl;
import com.mosaic.jk.utils.ListUtils;
import com.mosaic.jk.utils.StringUtils;

import java.io.File;
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
        config.version     = fetchDefaultVersionNumber();

        config.mainFQN     = fetchDefaultMainFQN( fs );

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

    private String fetchDefaultMainFQN( ProjectWorkspace project ) {
        return project.scanForMainClassFQN();
    }



    private File fetchSourceDirectory( File projectDirectory ) {
        return new File( projectDirectory, "src" );
    }

}
