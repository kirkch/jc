package com.mosaic.jk.env;

import com.mosaic.jk.config.Config;
import com.mosaic.jk.config.ConfigLoader;
import com.mosaic.jk.io.ProjectWorkspace;
import com.mosaic.jk.io.ProjectWorkspaceImpl;
import com.mosaic.jk.utils.VoidFunction0;

import java.io.File;
import java.io.PrintStream;

/**
 *
 */
public class EnvironmentImpl implements Environment {

    private ProjectWorkspace workspace;
    private Config           config;
    private BuildStats       buildStats;

    private PrintStream out = System.out;
    private PrintStream err = System.err;

    private long startMillis;



    public EnvironmentImpl(File rootDirectory) {
        this.workspace  = new ProjectWorkspaceImpl(rootDirectory);
        this.buildStats = new BuildStats(workspace);
    }


    public Config fetchConfig() {
        if ( config == null ) {
            config = new ConfigLoader(this).loadConfig();
        }

        return config;
    }

    public void appStarted() {
        startMillis = System.currentTimeMillis();

        buildStats.loadStats();
        buildStats.incrementBuildCount(getEnvironmentalBuildName());

        out.println( "JC");
    }

    public void appFinished() {
        long endMillis      = System.currentTimeMillis();
        long durationMillis = endMillis - startMillis;

        buildStats.appendDuration("total", durationMillis);
        buildStats.saveStats();

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

    public void timeAndInvokeJob(String jobName, VoidFunction0 job) {
        long startMillis = System.currentTimeMillis();

        try {
            job.invoke();
        } finally {
            long durationMillis = System.currentTimeMillis() - startMillis;

            buildStats.appendDuration(jobName, durationMillis);
        }
    }

    public void setCount(String entityName, int count) {
        buildStats.setCount(entityName, count);
    }

    /**
     * Defaults the build name to the logged in user's account name. Can be
     * overridden with -DbuildName=release
     */
    private String getEnvironmentalBuildName() {
        String userName     = System.getProperty("user.name");
        String envBuildName = System.getProperty("buildName", userName);

        return envBuildName;
    }

}
