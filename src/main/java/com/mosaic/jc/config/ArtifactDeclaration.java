package com.mosaic.jc.config;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class ArtifactDeclaration {
    public String artifactName;
    public String packagingType;   // eg  jar, fatJar, thinExecutableJar, libJar, war, ear
    public Set<String> includeModuleNames;


    public ArtifactDeclaration( String artifactName, String packagingType ) {
        this( artifactName, packagingType, new HashSet<String>() );
    }

    public ArtifactDeclaration( String artifactName, String packagingType, Set<String> includeModuleNames ) {
        this.artifactName       = artifactName;
        this.packagingType      = packagingType;
        this.includeModuleNames = includeModuleNames;
    }



    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public boolean equals( Object o ) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

}
