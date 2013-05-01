package com.mosaic.jk.env;

import java.io.PrintStream;

/**
 *
 */
public class EnvironmentImpl implements Environment {
    private PrintStream out = System.out;
    private PrintStream err = System.err;

    private long startMillis;

    @Override
    public void appStarted() {
        startMillis = System.currentTimeMillis();

        out.println( "JK");
    }

    @Override
    public void appFinished() {
        long endMillis = System.currentTimeMillis();
        long durationMillis = endMillis - startMillis;

        out.println( String.format("duration = %.2fs",durationMillis/1000.0) );
        out.println( "." );
    }

    public void info( String stage, String msg ) {
        err.println( String.format("[%s]: %s",stage, msg) );
    }

    @Override
    public void warn( String msg ) {
        err.println( "WARN: " + msg );
    }

    @Override
    public void error( String msg ) {
        err.println( "ERROR: " + msg );
    }
}
