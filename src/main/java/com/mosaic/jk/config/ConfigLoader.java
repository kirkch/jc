package com.mosaic.jk.config;

import com.mosaic.jk.env.Environment;
import com.mosaic.jk.io.IniFileDelegate;
import com.mosaic.jk.io.ProjectWorkspace;
import com.mosaic.jk.utils.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 *
 */
@SuppressWarnings("unchecked")
public class ConfigLoader {

    private Environment env;

    public ConfigLoader(Environment env) {
        this.env = env;
    }


    public Config loadConfig() {
        ProjectWorkspace project = env.getProjectWorkspace();
        File   projectDirectory = project.getRootProjectDirectory();
        Config config           = new Config();

        config.projectName          = fetchDefaultProjectName( projectDirectory );
        config.modules              = loadModuleInformation( project );
        config.groupId              = inferDefaultGroupId( config.modules );

        config.rootDirectory        = projectDirectory;
        config.destinationDirectory = new File( projectDirectory, "target/classes" );

        config.downloadRepositories = project.loadRepositories();

        try {
            Properties metaConfig = project.loadPropertiesFile( "meta" );

            config.version           = metaConfig.getProperty("version","0.0.1");
            config.versionFull       = config.version+"."+env.getEnvironmentalBuildType()+"_"+env.getBuildCount();
            config.javaVersion       = metaConfig.getProperty( "java", "1.6" );
            config.supportsSnapshots = Boolean.parseBoolean(metaConfig.getProperty("supportSnapshots","false"));


            loadDependenciesIntoModuleObjects( config.groupId, config.versionFull, config.modules, project );
        } catch ( IOException e ) {
            throw new RuntimeException(e);
        }


        return config;
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

        File[] sourceDirectories = fetchDefaultSourceDirectories( project );

        for ( File sourceDirectory : sourceDirectories ) {
            ModuleConfig module = new ModuleConfig();

            module.moduleNameNbl     = sourceDirectory.getName().equals("src") ? null : sourceDirectory.getName();
            module.sourceDirectories = new File[] {sourceDirectory};
            module.testDirectories   = fetchDefaultTestDirectories( project, module.moduleNameNbl );
            module.mainFQNs          = fetchDefaultMainFQN( module.sourceDirectories );
            module.packageType = "JAR";

            modules.add( module );
        }

        return modules;
    }

    private void loadDependenciesIntoModuleObjects( final String projectGroup, final String versionNumber, final List<ModuleConfig> modules, ProjectWorkspace project ) throws IOException {
        if ( !project.hasDependenciesFile() ) {
            List<Dependency> defaultDependencies = fetchDefaultDependencies();

            for ( ModuleConfig module : modules ) {
                module.dependencies = defaultDependencies;
            }

            return;
        }

        final DependencyParser dependencyParser = new DependencyParser( env, projectGroup, versionNumber );
        project.loadIniFile( "dependencies", new IniFileDelegate() {
            private String targetModuleNameNbl = null;

            public void parsingStarted() {}
            public void parsingFinished() {}


            public void labelRead( String label ) {
                targetModuleNameNbl = label;
            }

            public void lineRead( String line ) {
                Dependency dependency = dependencyParser.parseDependency( line );

                for ( ModuleConfig module : modules ) {
                    if ( targetModuleNameNbl == null || StringUtils.isEqualTo(module.moduleNameNbl, targetModuleNameNbl) ) {
                        module.dependencies.add( dependency );
                    }
                }
            }
        } );
    }

    private List<Dependency> fetchDefaultDependencies() {
        Dependency junit   = new Dependency( DependencyScope.TEST, "junit", "junit", "4.8.2" );
        Dependency mockito = new Dependency( DependencyScope.TEST, "org.mockito", "mockito-all", "1.9.5" );

        return Arrays.asList(junit,mockito);
    }

    private File[] fetchDefaultTestDirectories( ProjectWorkspace project, String moduleName ) {
        return project.scanForTestDirectories( moduleName );
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
