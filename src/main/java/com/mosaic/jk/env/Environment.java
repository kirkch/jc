package com.mosaic.jk.env;

import com.mosaic.jk.config.Config;
import com.mosaic.jk.io.ProjectWorkspace;
import com.mosaic.jk.utils.Function0;
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

    void invokeAndTimeJob(String jobName, VoidFunction0 job);
    <T> T invokeAndTimeJob(String jobName, Function0<T> job);

    void setCount(String entityName, int count);
}
