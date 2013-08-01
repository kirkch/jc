package com.mosaic.jc.env;

import com.mosaic.jc.io.ProjectWorkspace;
import com.mosaic.jc.utils.Function0;
import com.mosaic.jc.utils.VoidFunction0;
import com.mosaic.jc.config.Config;
import com.mosaic.jc.io.ProjectWorkspace;
import com.mosaic.jc.utils.Function0;
import com.mosaic.jc.utils.VoidFunction0;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class EnvironmentFake implements Environment {
    public List<Event> recordedEvents = new ArrayList<Event>();

    public Config fetchConfig() {
        return null;
    }

    public void appStarted() {
        recordedEvents.add( new Event("started") );
    }

    public void appFinished() {
        recordedEvents.add( new Event("finished") );
    }

    public void info( String stage, String msg ) {
        recordedEvents.add( new Event("INFO("+stage+"): "+ msg) );
    }

    public void error( String msg ) {
        recordedEvents.add( new Event("ERROR: "+ msg) );
    }

    public void warn( String msg ) {
        recordedEvents.add( new Event("WARN: "+ msg) );
    }

    public ProjectWorkspace getProjectWorkspace() {
        return null;
    }

    public void invokeAndTimeJob(String jobName, VoidFunction0 job) {

    }

    public <T> T invokeAndTimeJob(String jobName, Function0<T> job) {
        return null;
    }

    public void setCount(String entityName, int count) {
    }

    public long getBuildCount() {
        return 1;
    }

    public String getEnvironmentalBuildType() {
        return "dev";
    }

    public static class Event {
        public final String desc;

        public Event( String desc ) {
            this.desc = desc;
        }

        public int hashCode() {
            return desc.hashCode();
        }

        public String toString() {
            return desc;
        }

        public boolean equals( Object o ) {
            if ( !(o instanceof Event) ) {
                return false;
            }

            Event other = (Event) o;
            return this.desc.equals(other.desc);
        }
    }

}
