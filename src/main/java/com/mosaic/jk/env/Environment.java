package com.mosaic.jk.env;

import com.mosaic.jk.config.Config;
import com.mosaic.jk.io.ProjectWorkspace;

/**
 *
 */
public interface Environment {

    Config fetchConfig();

    void appStarted();
    void appFinished();

    void info( String stage, String msg );
    void error( String msg );

    void warn( String msg );

    ProjectWorkspace getProjectWorkspace();
}
