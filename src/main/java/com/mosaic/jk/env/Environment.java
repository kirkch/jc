package com.mosaic.jk.env;

/**
 *
 */
public interface Environment {
    void appStarted();

    void appFinished();

    public void info( String stage, String msg );
    void error( String msg );
}
