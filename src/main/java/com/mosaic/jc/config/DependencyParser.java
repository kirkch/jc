package com.mosaic.jc.config;


import com.mosaic.jc.env.Environment;
import com.mosaic.jc.utils.StringUtils;
import com.mosaic.jc.env.Environment;
import com.mosaic.jc.utils.StringUtils;

/**
 * Parses encoded declarations of a dependency
 */
public class DependencyParser {

    private Environment env;
    private String      projectGroup;
    private String      projectVersion;

    public DependencyParser( Environment env, String projectGroup, String projectVersion ) {
        this.env            = env;
        this.projectGroup   = projectGroup;
        this.projectVersion = projectVersion;
    }


    public Dependency parseDependency( String encodedDependency ) {
        if ( StringUtils.isBlank(encodedDependency) ) {
            return null;
        }

        DependencyScope scope = DependencyScope.COMPILE;
        int indexOfScopeStart = encodedDependency.indexOf('<');
        if ( indexOfScopeStart > 0 ) {
            int indexOfScopeEnd = encodedDependency.lastIndexOf('>');
            if ( indexOfScopeEnd <= 0 ) {
                env.error("malformed dependency '"+encodedDependency+"'");
                return null;
            }

            String scopeString     = encodedDependency.substring( indexOfScopeStart+1, indexOfScopeEnd ).trim();

            if ( scopeString.equals("test") ) {
                scope = DependencyScope.TEST;
            } else if ( scopeString.equals("runtime") ) {
                scope = DependencyScope.RUNTIME;
            } else if ( scopeString.equals("provided") ) {
                scope = DependencyScope.PROVIDED;
            } else if ( scopeString.equals("compile") ) {
                scope = DependencyScope.COMPILE;
            } else {
                env.error("unrecognized scope in dependency '"+encodedDependency+"'");
                return null;
            }

            encodedDependency = encodedDependency.substring(0,indexOfScopeStart);
        }

        String[] components = encodedDependency.split( ":" );
        StringUtils.trimArray(components);

        switch ( components.length ) {
            case 1: return new Dependency(scope, projectGroup, encodedDependency, projectVersion, null, true);
            case 2: env.error("missing version in dependency '"+encodedDependency+"'"); return null;
            case 3: return new Dependency(scope, components[0], components[1], components[2], null, false);
            case 4: return new Dependency(scope, components[0], components[1], components[2], components[3], false);
            default: return null;
        }
    }


}
