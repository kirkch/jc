package com.mosaic.jc;

import com.mosaic.jc.compilers.java.JavaCompiler;
import com.mosaic.jc.config.Config;
import com.mosaic.jc.env.Environment;
import com.mosaic.jc.env.EnvironmentImpl;
import com.mosaic.jc.maven.POMWriter;
import com.mosaic.jc.utils.FileUtils;
import com.mosaic.jc.compilers.java.JavaCompiler;
import com.mosaic.jc.config.Config;
import com.mosaic.jc.env.Environment;
import com.mosaic.jc.env.EnvironmentImpl;
import com.mosaic.jc.maven.POMWriter;
import com.mosaic.jc.utils.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 */
public class Main {

    public static void main( String[] args ) throws IOException {
        File        rootDirectory = getRootDirectoryFromArgs( args );
        Environment env           = new EnvironmentImpl( rootDirectory );

        env.appStarted();

        try {
            Config config = env.fetchConfig();

            if ( validateConfig(env) ) {
                POMWriter out = new POMWriter( env, new BufferedWriter(new FileWriter(new File(rootDirectory,"pom.xml"))) );
                out.writeToPOM( config );

                JavaCompiler compiler = new JavaCompiler();
                compiler.compile(env);
            }
        } finally {
            env.appFinished();
        }
    }

    private static File getRootDirectoryFromArgs( String[] args ) {
        if ( args.length > 0 ) {
            return new File(args[0]);
        }

        return FileUtils.getWorkingDirectory();
    }

    private static boolean validateConfig(Environment env) {
        File rootDirectory = env.getProjectWorkspace().getRootProjectDirectory();

        if ( !rootDirectory.exists() ) {
            env.error( "specified root directory '"+rootDirectory.getPath()+"' does not exist" );
            return false;
        }

        return true;
    }

}
