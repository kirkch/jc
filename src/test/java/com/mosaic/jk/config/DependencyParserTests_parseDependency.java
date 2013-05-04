package com.mosaic.jk.config;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class DependencyParserTests_parseDependency {

    private DependencyParser parser = new DependencyParser( "com.maverik", "1.0" );

    @Test
    public void givenEmptyString_expectNull() {
        Dependency d = parser.parseDependency( "" );

        assertEquals( null, d );
    }

    @Test
    public void givenNull_expectNull() {
        Dependency d = parser.parseDependency( null );

        assertEquals( null, d );
    }

    @Test
    public void givenASingleWord_expectDependencyWithDefaultGroupAndVersionNumber() {
        Dependency d = parser.parseDependency( "fooBar" );

        assertEquals( new Dependency("com.maverik", "fooBar", "1.0"), d );
    }

    @Test
    public void givenASingleWord_expectDependencyWithSuppliedScope() {
        Dependency d = parser.parseDependency( "fooBar" );

        assertEquals( new Dependency("com.maverik", "fooBar", "1.0" ), d );
    }

    @Test
    public void givenSpecifiedGroupArtAndVersion_expectMatchingDependency() {
        Dependency d = parser.parseDependency( "org.junit:junit:1.1" );

        assertEquals( new Dependency( "org.junit", "junit", "1.1" ), d );
    }

    @Test
    public void givenPackageInfo_expectDepencyWithPackage() {
        Dependency d = parser.parseDependency( "org.junit:junit:1.1:org.junit.proto" );

        assertEquals( new Dependency("org.junit", "junit", "1.1", "org.junit.proto"), d );
    }

}
