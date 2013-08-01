package com.mosaic.jc.config;

import com.mosaic.jc.config.Dependency;
import com.mosaic.jc.config.DependencyParser;
import com.mosaic.jc.config.DependencyScope;
import com.mosaic.jc.env.EnvironmentFake;
import org.junit.Test;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;

/**
 *
 */
public class DependencyParserTests_parseDependency {

    private EnvironmentFake  env    = new EnvironmentFake();
    private DependencyParser parser = new DependencyParser( env, "com.maverik", "1.0" );

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

        Dependency fooBar = new Dependency(DependencyScope.COMPILE, "com.maverik", "fooBar", "1.0");
        fooBar.projectModuleFlag = true;

        assertEquals(fooBar, d );
    }

    @Test
    public void givenASingleWord_expectDependencyWithSuppliedScope() {
        Dependency d = parser.parseDependency( "fooBar" );

        Dependency fooBar = new Dependency(DependencyScope.COMPILE, "com.maverik", "fooBar", "1.0");
        fooBar.projectModuleFlag = true;

        assertEquals(fooBar, d );
    }

    @Test
    public void givenSpecifiedGroupArtAndVersion_expectMatchingDependency() {
        Dependency d = parser.parseDependency( "org.junit:junit:1.1" );

        assertEquals( new Dependency( DependencyScope.COMPILE, "org.junit", "junit", "1.1" ), d );
    }

    @Test
    public void givenPackageInfo_expectDependencyWithPackage() {
        Dependency d = parser.parseDependency( "org.junit:junit:1.1:org.junit.proto" );

        assertEquals( new Dependency(DependencyScope.COMPILE, "org.junit", "junit", "1.1", "org.junit.proto", false), d );
    }

    @Test
    public void givenTestScope() {
        Dependency d = parser.parseDependency( "org.junit:junit:1.1 <test>" );

        assertEquals( new Dependency(DependencyScope.TEST, "org.junit", "junit", "1.1"), d );
    }

    @Test
    public void givenCompileScope() {
        Dependency d = parser.parseDependency( "org.junit:junit:1.1 <compile>" );

        assertEquals( new Dependency(DependencyScope.COMPILE, "org.junit", "junit", "1.1"), d );
    }

    @Test
    public void givenRuntimeScope() {
        Dependency d = parser.parseDependency( "org.junit:junit:1.1 <runtime>" );

        assertEquals( new Dependency(DependencyScope.RUNTIME, "org.junit", "junit", "1.1"), d );
    }

    @Test
    public void givenProvidedScope() {
        Dependency d = parser.parseDependency( "org.junit:junit:1.1 <provided>" );

        assertEquals( new Dependency(DependencyScope.PROVIDED, "org.junit", "junit", "1.1"), d );
    }

    @Test
    public void givenUnknownScope_expectNullAndErrorMessage() {
        Dependency d = parser.parseDependency( "org.junit:junit:1.1 <foo>" );

        assertNull(d);
        assertEquals(new EnvironmentFake.Event("ERROR: unrecognized scope in dependency 'org.junit:junit:1.1 <foo>'"), env.recordedEvents.get(0));
    }

    @Test
    public void givenMalformedScope_expectNullAndErrorMessage() {
        Dependency d = parser.parseDependency( "org.junit:junit:1.1 < " );

        assertNull(d);
        assertEquals(new EnvironmentFake.Event("ERROR: malformed dependency 'org.junit:junit:1.1 < '"), env.recordedEvents.get(0));
    }

    @Test
    public void givenMissingScope_expectNullAndErrorMessage() {
        Dependency d = parser.parseDependency( "org.junit:junit:1.1 < > " );

        assertNull(d);
        assertEquals(new EnvironmentFake.Event("ERROR: unrecognized scope in dependency 'org.junit:junit:1.1 < > '"), env.recordedEvents.get(0));
    }

    @Test
    public void givenMissingaVersion_expectNullAndErrorMessage() {
        Dependency d = parser.parseDependency( "org.junit:junit" );

        assertNull(d);
        assertEquals(new EnvironmentFake.Event("ERROR: missing version in dependency 'org.junit:junit'"), env.recordedEvents.get(0));
    }


}
