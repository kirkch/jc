package com.mosaic.jk;

import com.mosaic.jk.compilers.java.JavaCompiler;
import com.mosaic.jk.config.Config;
import com.mosaic.jk.config.ConfigLoader;
import com.mosaic.jk.env.Environment;
import com.mosaic.jk.env.EnvironmentImpl;
import com.mosaic.jk.maven.POMWriter;
import com.mosaic.jk.utils.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 */
public class Main {

    public static void main( String[] args ) throws IOException {
        Environment env = new EnvironmentImpl();

        env.appStarted();

        try {
            File   rootDirectory = getRootDirectoryFromArgs( args );
            Config config        = loadConfig(env, rootDirectory);

            if ( validateConfig(env,rootDirectory) ) {
                POMWriter out = new POMWriter( env, new FileWriter(new File(rootDirectory,"pom.xml")) );
                out.writeToPOM( config );

                JavaCompiler compiler = new JavaCompiler();
                compiler.compile(env, config);
            }
        } finally {
            env.appFinished();
        }
    }

    private static Config loadConfig( Environment env, File rootDirectory ) {
        return new ConfigLoader(env).loadConfigFor( rootDirectory );
    }

    private static File getRootDirectoryFromArgs( String[] args ) {
        if ( args.length > 0 ) {
            return new File(args[0]);
        }

        return FileUtils.getWorkingDirectory();
    }

    private static boolean validateConfig(Environment env, File rootDirectory) {
        if ( !rootDirectory.exists() ) {
            env.error( "specified root directory '"+rootDirectory.getPath()+"' does not exist" );
            return false;
        }

        return true;
    }

}
