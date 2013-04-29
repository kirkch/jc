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

    public static Dependency test( String group, String artefact, String version ) {
        Dependency d = new Dependency();

        d.groupId       = "junit";
        d.artefactName  = "junit";
        d.versionNumber = "4.8.2";
        d.scope         = TEST;

        return d;
    }

    public static enum DependencyScope {
        TEST, COMPILE, RUNTIME
    }


    public String          groupId;
    public String          artefactName;
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
