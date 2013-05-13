package com.mosaic.jk.config;

import org.apache.commons.lang3.StringUtils;

/**
 * Parses encoded declarations of a dependency
 */
public class DependencyParser {

    private String projectGroup;
    private String progectVersion;

    public DependencyParser( String projectGroup, String projectVersion ) {
        this.projectGroup   = projectGroup;
        this.progectVersion = projectVersion;
    }


    public Dependency parseDependency( String encodedDependency ) {
        if ( StringUtils.isBlank(encodedDependency) ) {
            return null;
        }

        String[] components = encodedDependency.split( ":" );

        switch ( components.length ) {
            case 1: return new Dependency(projectGroup, encodedDependency, progectVersion, null, true);
            case 3: return new Dependency(components[0], components[1], components[2], null, false);
            case 4: return new Dependency(components[0], components[1], components[2], components[3], false);
            default: return null;
        }
    }


}
