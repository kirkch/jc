package com.mosaic.jk;

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
        File rootDirectory = getRootDirectoryFromArgs( args );

        Main main = new Main(env);
        main.setRootDirectory( rootDirectory );

        if ( main.validateConfig() ) {
            POMWriter out = new POMWriter( env, new FileWriter(new File(rootDirectory,"pom.xml")) );
            main.generateMavenPOM( out );
        }
    }

    private static File getRootDirectoryFromArgs( String[] args ) {
        if ( args.length > 0 ) {
            return new File(args[0]);
        }

        return FileUtils.getWorkingDirectory();
    }



    private File rootDirectory;
    private Environment env;

    public Main( Environment env ) {
        this.env = env;
    }

    public void setRootDirectory( File rootDirectory ) {
        this.rootDirectory = rootDirectory;
    }

    public boolean validateConfig() {
        if ( !rootDirectory.exists() ) {
            env.error( "specified root directory '"+rootDirectory.getPath()+"' does not exist" );
            return false;
        }

        return true;
    }

    public void generateMavenPOM( POMWriter out ) {
        env.appStarted();

        try {
            if ( !validateConfig() ) {
                return;
            }

            Config config = loadConfig();

            out.writeToPOM( config );
        } finally {
            env.appFinished();
        }
    }

    private Config loadConfig() {
        return new ConfigLoader().loadConfigFor( rootDirectory );
    }

}
