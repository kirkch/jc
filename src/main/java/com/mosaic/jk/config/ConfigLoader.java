package com.mosaic.jk.config;

import com.mosaic.jk.io.IniFileDelegate;
import com.mosaic.jk.io.ProjectWorkspace;
import com.mosaic.jk.io.ProjectWorkspaceImpl;
import com.mosaic.jk.utils.FileUtils;
import com.mosaic.jk.utils.ListUtils;
import com.mosaic.jk.utils.StringUtils;
import com.mosaic.jk.utils.VoidFunction1;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class ConfigLoader {

    public Config loadConfigFor( File projectDirectory ) {
        ProjectWorkspace project = new ProjectWorkspaceImpl( projectDirectory );

        Config config = new Config();

        config.projectName          = fetchDefaultProjectName( projectDirectory );
        config.versionNumber        = fetchDefaultVersionNumber();
        config.modules              = loadModuleInformation( project );
        config.groupId              = inferDefaultGroupId( config.modules );

        config.rootDirectory        = projectDirectory;
        config.destinationDirectory = new File( projectDirectory, "target/classes" );

        try {
            loadDependenciesIntoModuleObjects( config.projectName, config.versionNumber, config.modules, project );
        } catch ( IOException e ) {
            throw new RuntimeException(e);
        }


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

    private String inferDefaultGroupId( List<ModuleConfig> modules ) {
        // Infers by sorting the full package names of the files under each source directory and then returning the
        // first two elements of the first package. Else 'sandbox' as a default.
        List<String> candidateGroupIds = new ArrayList<String>();

        for ( ModuleConfig module : modules ) {
            for ( File sourceDirectory : module.sourceDirectories ) {
                candidateGroupIds.add( inferDefaultGroupId(sourceDirectory) );
            }
        }

        Collections.sort(candidateGroupIds);

        if ( candidateGroupIds.size() == 0 ) {
            return "sandbox";
        }

        return candidateGroupIds.get(0);
    }

    private String inferDefaultGroupId( File sourceDirectory ) {
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

    private List<ModuleConfig> loadModuleInformation( ProjectWorkspace project ) {
        List<ModuleConfig> modules = new ArrayList<ModuleConfig>();

        ModuleConfig module = new ModuleConfig();

        module.sourceDirectories = fetchDefaultSourceDirectories( project );
        module.testDirectories   = fetchDefaultTestDirectories( project );
        module.mainFQNs          = fetchDefaultMainFQN( module.sourceDirectories );
        module.packageType = "JAR";

        modules.add( module );

        return modules;
    }

    private void loadDependenciesIntoModuleObjects( final String projectName, final String versionNumber, final List<ModuleConfig> modules, ProjectWorkspace project ) throws IOException {
        if ( !project.hasDependenciesFile() ) {
            List<Dependency> defaultDependencies = fetchDefaultDependencies();

            for ( ModuleConfig module : modules ) {
                module.dependencies = defaultDependencies;
            }

            return;
        }

        final DependencyParser dependencyParser = new DependencyParser( projectName, versionNumber );
        project.loadIniFile( "dependencies", new IniFileDelegate() {
            public void parsingStarted() {}
            public void parsingFinished() {}


            public void labelRead( String label ) {
            }

            public void lineRead( String line ) {
                Dependency dependency = dependencyParser.parseDependency( line );

                for ( ModuleConfig module : modules ) {
                    module.dependencies.add( dependency );
                }
            }
        } );



     // todo assign defaults
//        return fetchDefaultDependencies();
    }

    private List<Dependency> fetchDefaultDependencies() {
        Dependency junit   = new Dependency( "junit", "junit", "4.8.2" );
        Dependency mockito = new Dependency( "org.mockito", "mockito-all", "1.9.5" );

        return Arrays.asList(junit,mockito);
    }

    private File[] fetchDefaultTestDirectories( ProjectWorkspace project ) {
        return project.scanForTestDirectories();
    }

    private File[] fetchDefaultSourceDirectories( ProjectWorkspace project ) {
        return project.scanForSourceDirectories();
    }


    private String[] fetchDefaultMainFQN( File[] sourceDirectories ) {
        final List<String> mainClasses = new ArrayList<String>();

        for ( final File sourceDirectory : sourceDirectories ) {
            FileUtils.depthFirstScan( sourceDirectory, new VoidFunction1<File>() {
                public void invoke( File f ) {
                    if ( f.isFile() && f.getName().endsWith( "Main.java" ) ) {
                        String relativePath = FileUtils.toRelativePath( sourceDirectory, f );

                        relativePath = relativePath.substring( 0, relativePath.length()-5 ); // strip off .java

                        mainClasses.add( relativePath.replaceAll( "/", "." ) );
                    }
                }
            } );
        }

        return mainClasses.toArray( new String[mainClasses.size()] );
    }


}
