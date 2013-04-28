package com.mosaic.jk.maven;

import com.mosaic.jk.config.Config;
import com.mosaic.jk.env.Environment;
import com.mosaic.jk.io.XMLWriter;

import java.io.Writer;

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
        String mavenArtefactName  = config.projectName.replaceAll( " ", "-" ).toLowerCase();
        String mavenVersionNumber = config.version + "-SNAPSHOT";

        env.info( "Maven", "Generating POM file" );

        startPOM( config.groupId, mavenArtefactName, mavenVersionNumber );
        printManifestPlugin( config );
        endPOM();
    }



    private void startPOM( String groupId, String artefactId, String version ) {
        out.println( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" );
        out.println( "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">" );
        out.println();
        out.printTagWithTextBody( "modelVersion", "4.0.0" );
        out.println();
        out.printTagWithTextBody( "groupId", groupId );
        out.printTagWithTextBody( "artifactId", artefactId );
        out.printTagWithTextBody( "version", version );
        out.println();
        out.printTagWithTextBody( "packaging", "jar" );
        out.println();
    }

    private void endPOM() {
        out.println( "</project>" );
        out.flush();
        out.close();
    }



    private void printManifestPlugin( Config config ) {
        out.printStartTag("build" );

        out.printOnelineTag( "sourceDirectory", "src" );

        out.printStartTags("plugins", "plugin");

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
                out.printOnelineTag("mainClass", config.mainFQN);
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

        out.printEndTags( "plugin", "plugins", "build" );
    }




}
