package com.mosaic.jc.env;

import com.mosaic.jc.io.ProjectWorkspace;
import com.mosaic.jc.utils.Function0;
import com.mosaic.jc.config.Config;
import com.mosaic.jc.io.ProjectWorkspace;
import com.mosaic.jc.utils.Function0;
import com.mosaic.jc.utils.VoidFunction0;

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

    public long getBuildCount();
    public String getEnvironmentalBuildType();
}
