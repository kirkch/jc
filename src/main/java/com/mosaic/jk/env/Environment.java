package com.mosaic.jk.env;

import com.mosaic.jk.config.Config;
import com.mosaic.jk.io.ProjectWorkspace;
import com.mosaic.jk.utils.VoidFunction0;

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

    void demarcateJob( String jobName, VoidFunction0 job );
}
