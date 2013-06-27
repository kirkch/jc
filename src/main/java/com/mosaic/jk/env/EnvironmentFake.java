package com.mosaic.jk.env;

import com.mosaic.jk.config.Config;
import com.mosaic.jk.io.ProjectWorkspace;
import com.mosaic.jk.utils.VoidFunction0;

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

    public void timeAndInvokeJob(String jobName, VoidFunction0 job) {

    }

    public void setCount(String entityName, int count) {
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
