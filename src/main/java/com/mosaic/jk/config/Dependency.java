package com.mosaic.jk.config;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 *
 */
public class Dependency {

    public Dependency() {}

    public Dependency( String group, String artifact, String version ) {
        this( group, artifact, version, null );
    }

    public Dependency( String group, String artifact, String version, String packageNbl ) {
        this.groupId       = group;
        this.artifactName  = artifact;
        this.versionNumber = version;
        this.packageNbl    = packageNbl;
    }

    public String groupId;
    public String artifactName;
    public String versionNumber;
    public String packageNbl;


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
