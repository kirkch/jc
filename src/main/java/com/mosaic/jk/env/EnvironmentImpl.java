package com.mosaic.jk.env;

import com.mosaic.jk.config.Config;
import com.mosaic.jk.config.ConfigLoader;
import com.mosaic.jk.io.ProjectWorkspace;
import com.mosaic.jk.io.ProjectWorkspaceImpl;

import java.io.File;
import java.io.PrintStream;

/**
 *
 */
public class EnvironmentImpl implements Environment {

    private ProjectWorkspace workspace;
    private Config           config;

    private PrintStream out = System.out;
    private PrintStream err = System.err;

    private long startMillis;



    public EnvironmentImpl(File rootDirectory) {
        this.workspace = new ProjectWorkspaceImpl(rootDirectory);
    }


    public Config fetchConfig() {
        if ( config == null ) {
            config = new ConfigLoader(this).loadConfig();
        }

        return config;
    }

    public void appStarted() {
        startMillis = System.currentTimeMillis();

        out.println( "JC");
    }

    public void appFinished() {
        long endMillis      = System.currentTimeMillis();
        long durationMillis = endMillis - startMillis;

        out.println( String.format("Total duration = %.2fs",durationMillis/1000.0) );
        out.println( "." );
    }

    public void info( String stage, String msg ) {
        err.println( String.format("[%s]: %s",stage, msg) );
    }

    public void warn( String msg ) {
        err.println( "WARN: " + msg );
    }


    public ProjectWorkspace getProjectWorkspace() {
        return workspace;
    }

    public void error( String msg ) {
        err.println( "ERROR: " + msg );
    }

}
