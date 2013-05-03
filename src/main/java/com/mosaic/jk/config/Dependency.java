package com.mosaic.jk.config;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import static com.mosaic.jk.config.Dependency.DependencyScope.TEST;

/**
 *
 */
public class Dependency {

    public static Dependency test( String group, String artifact, String version ) {
        return new Dependency( group, artifact, version, TEST );
    }

    public static enum DependencyScope {
        TEST, COMPILE, RUNTIME
    }

    public Dependency() {}

    public Dependency(String group, String artifact, String version, DependencyScope scope) {
        this.groupId       = group;
        this.artifactName  = artifact;
        this.versionNumber = version;
        this.scope         = scope;
    }

    public String          groupId;
    public String          artifactName;
    public String          versionNumber;

    public String          packageNbl;

    public DependencyScope scope;



    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode( this );
    }

    public String toString() {
        return ToStringBuilder.reflectionToString( this, ToStringStyle.SHORT_PREFIX_STYLE );
    }

    public boolean equals( Object o ) {
        return EqualsBuilder.reflectionEquals( this, o );
    }

}
