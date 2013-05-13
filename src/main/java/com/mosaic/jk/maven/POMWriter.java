package com.mosaic.jk.maven;

import com.mosaic.jk.config.Config;
import com.mosaic.jk.config.Dependency;
import com.mosaic.jk.config.ModuleConfig;
import com.mosaic.jk.env.Environment;
import com.mosaic.jk.io.XMLWriter;
import com.mosaic.jk.utils.FileUtils;

import java.io.File;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 */
public class POMWriter {

    private XMLWriter   out;
    private Environment env;


    public POMWriter( Environment env, Writer out ) {
        this.env = env;
        this.out = new XMLWriter(out);
    }


    public void writeToPOM( Config config ) {
        String mavenArtifactName  = config.projectName.replaceAll( " ", "-" ).toLowerCase();
        String mavenVersionNumber = config.versionNumber + "-SNAPSHOT";

        env.info( "Maven", "Generating POM file" );

        startPOM( config.groupId, mavenArtifactName, mavenVersionNumber );

        startPluginsBlock( config );
        printSureFireTestPlugin();
        printManifestPlugin( config );
//        printMavenBuilderHelperPlugin( config );
        endPluginsBlock();
        printDependencies( config );

        endPOM();
    }

    private void startPOM( String groupId, String artifactId, String version ) {
        out.println( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" );
        out.println( "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">" );
        out.println();
        out.printTagWithTextBody( "modelVersion", "4.0.0" );
        out.println();
        out.printTagWithTextBody( "groupId", groupId );
        out.printTagWithTextBody( "artifactId", artifactId );
        out.printTagWithTextBody( "version", version );
        out.println();
        out.printTagWithTextBody( "packaging", "jar" );
        out.println();

    }


    private void printResourceDirectories( Config config ) {
        out.printStartTag( "resources" );
        for ( ModuleConfig module : config.modules ) {
            for ( File f : module.sourceDirectories ) {
                out.printStartTag( "resource" );
                out.printOnelineTag( "directory", FileUtils.toRelativePath(config.rootDirectory,f) );
                out.printEndTag( "resource" );
            }
        }

        out.printEndTag( "resources" );

        out.printStartTag( "testResources" );
        for ( ModuleConfig module : config.modules ) {
            for ( File f : module.testDirectories ) {
                out.printStartTag( "testResource" );
                out.printOnelineTag( "directory", FileUtils.toRelativePath(config.rootDirectory,f) );
                out.printEndTag( "testResource" );
            }
        }

        out.printEndTag( "testResources" );
    }

//        <resources>
//        <resource>
//        <directory>...</directory> will be added as Source folder
//            </resource>
//        </resources>
//
//        <testResources>
//        <testResource>
//        <directory>...</directory> will be added as Test Source folder
//        </testResource>
//        </testResources>


    private void endPOM() {
        out.println( "</project>" );
        out.flush();
        out.close();
    }

//    <plugin>
//    <groupId>org.apache.maven.plugins</groupId>
//    <artifactId>maven-surefire-plugin</artifactId>
//    <version>2.14.1</version>
//    <configuration>
//    <includes>
//    <include>**/*Test*.java</include>
//                    </includes>
//                </configuration>
//            </plugin>
    private void printSureFireTestPlugin() {
        out.printStartTag( "plugin" );

        out.printOnelineTag( "groupId", "org.apache.maven.plugins" );
        out.printOnelineTag( "artifactId", "maven-surefire-plugin" );
        out.printOnelineTag( "version", "2.14.1" );


        out.printStartTags( "configuration", "includes" );
        out.printOnelineTag( "include", "**/*Test*.java" );
        out.printEndTags( "includes", "configuration" );

        out.printEndTag( "plugin" );
    }


//    <plugin>
//    <groupId>org.codehaus.mojo</groupId>
//    <artifactId>build-helper-maven-plugin</artifactId>
//    <executions>
//    <execution>
//    <phase>generate-sources</phase>
//    <goals><goal>add-source</goal></goals>
//    <configuration>
//    <sources>
//    <source>src/main/generated</source>
//    </sources>
//    </configuration>
//    </execution>
//    </executions>
//    </plugin>
//    private void printMavenBuilderHelperPlugin( Config config ) {  // declares source locations to maven and ide's
//        out.printStartTag( "plugin" );
//
//        out.printOnelineTag( "groupId", "org.codehaus.mojo" );
//        out.printOnelineTag( "artifactId", "build-helper-maven-plugin" );
////        out.printOnelineTag( "version", "2.14.1" );
//
//
//        out.printStartTags( "executions" );
//
//        {
//            out.printStartTags( "execution" );
//            out.printOnelineTag( "id", "generate-sources" );
//            out.printOnelineTag( "phase", "generate-sources" );
//            out.printStartTags( "goals" );
//            out.printOnelineTag( "goal", "add-source" );
//            out.printEndTags( "goals" );
//
//            out.printStartTags( "configuration", "sources" );
//
//            for ( ModuleConfig module : config.modules ) {
//                for ( File dir : module.sourceDirectories ) {
//                    out.printOnelineTag( "source", dir.getAbsolutePath() );
//                }
//            }
//
//            out.printEndTags( "sources", "configuration" );
//            out.printEndTags( "execution");
//        }
//
//
//        {
//            out.printStartTags( "execution" );
//            out.printOnelineTag( "id", "generate-test-sources" );
//            out.printOnelineTag( "phase", "generate-test-sources" );
//            out.printStartTags( "goals" );
//            out.printOnelineTag( "goal", "add-test-source" );
//            out.printEndTags( "goals" );
//
//            out.printStartTags( "configuration", "sources" );
//
//            for ( ModuleConfig module : config.modules ) {
//                for ( File dir : module.testDirectories ) {
//                    out.printOnelineTag( "source", dir.getAbsolutePath() );
//                }
//            }
//
//            out.printEndTags( "sources", "configuration" );
//            out.printEndTags( "execution");
//        }
//
//        out.printEndTags( "executions" );
//
//        out.printEndTag( "plugin" );
//    }


    private void startPluginsBlock( Config config) {
        out.printStartTag("build" );

        out.printOnelineTag( "sourceDirectory", "src" );
        printResourceDirectories( config );

        out.printStartTags("plugins");
    }


    private void endPluginsBlock() {
        out.printEndTags( "plugins", "build" );
    }

    private void printManifestPlugin( Config config ) {
        out.printStartTags("plugin");

        out.printOnelineTag("artifactId", "maven-assembly-plugin");
        out.printOnelineTag("version", "2.4");

        {
            out.printStartTag("configuration");

            {
                out.printOnelineTag("appendAssemblyId", "false");

                out.printStartTag("descriptorRefs");
                out.printOnelineTag("descriptorRef", "jar-with-dependencies");
                out.printEndTag( "descriptorRefs" );

                out.printStartTag("archive");
                out.printStartTag("manifest");

                List<String> mainClasses = config.allMainFQNs();
                int numMains = mainClasses.size();
                if ( numMains > 0 ) {
                    String fqn = mainClasses.get( 0 );
                    if ( numMains > 1 ) {
                        env.warn( "Found " + numMains + " classes with Main in the name. We picked the first one to place into the POM: " + fqn);
                    }

                    out.printOnelineTag( "mainClass", fqn );
                }

                out.printEndTag( "manifest" );
                out.printEndTag( "archive" );
            }

            out.printEndTag( "configuration" );
        }


        out.printStartTags("executions", "execution");
        out.printOnelineTag("id", "make-assembly");
        out.printOnelineTag("phase", "package");


        out.printStartTag("goals");
        out.printOnelineTag("goal", "assembly");
        out.printEndTag( "goals" );


        out.printEndTags("execution", "executions");

        out.printEndTags( "plugin" );
    }



    private void printDependencies( Config config ) {
        out.printStartTag( "dependencies" );

        Set<Dependency> dependenciesSoFar = new HashSet<Dependency>();
        for ( ModuleConfig module : config.modules ) {
            for ( Dependency d : module.dependencies ) {
                if ( !dependenciesSoFar.contains(d) ) {
                    printDependency(d);

                    dependenciesSoFar.add(d);
                }
            }
        }

        out.printEndTag( "dependencies" );
    }

    private void printDependency( Dependency d ) {
        out.printStartTag( "dependency" );
        out.printOnelineTag( "groupId", d.groupId );
        out.printOnelineTag( "artifactId", d.artifactName );
        out.printOnelineTag( "version", d.versionNumber );
        out.printEndTag( "dependency" );
    }

}
