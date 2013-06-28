package com.mosaic.jk.env;

import com.mosaic.jk.io.ProjectWorkspace;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 *
 */
public class BuildStatsTest {

    private ProjectWorkspace workspaceMock = mock(ProjectWorkspace.class);
    private BuildStats       stats         = new BuildStats(workspaceMock);


    @Test
    public void storeFileStraightAway_expectWriteBlankProperties() {
        stats.saveStats();

        Mockito.verify(workspaceMock).writePropertiesFile("buildstats",new Properties());
    }

    @Test
    public void loadStore_expectToStoreWhatWasLoaded() {
        Properties properties = createInitialProperties();


        Mockito.when(workspaceMock.loadPropertiesFile("buildstats")).thenReturn(properties);

        stats.loadStats();
        stats.saveStats();

        Mockito.verify(workspaceMock).writePropertiesFile("buildstats",properties);
    }

    @Test
    public void storeDuration_expectSingleDuration() {
        Properties properties = createInitialProperties();

        Mockito.when(workspaceMock.loadPropertiesFile("buildstats")).thenReturn(properties);

        stats.loadStats();
        stats.appendDuration("compile", 600);
        stats.saveStats();

        Properties expectedProperties = (Properties) properties.clone();
        expectedProperties.setProperty("build_duration_compile", "0.6");


        Mockito.verify(workspaceMock).writePropertiesFile("buildstats",expectedProperties);
    }

    @Test
    public void givenOneDurationAtLoad_storeDuration_expectTwoDurationList() {
        Properties properties = createInitialProperties();
        properties.setProperty("build_duration_compile", "0.6");

        Mockito.when(workspaceMock.loadPropertiesFile("buildstats")).thenReturn(properties);


        stats.loadStats();
        stats.appendDuration("compile", 1640);
        stats.saveStats();

        Properties expectedProperties = (Properties) properties.clone();
        expectedProperties.setProperty("build_duration_compile", "0.6,1.64");


        Mockito.verify(workspaceMock).writePropertiesFile("buildstats",expectedProperties);
    }

    @Test
    public void givenTwoDurationAtLoad_storeDuration_expectThreeDurationList() {
        Properties properties = createInitialProperties();
        properties.setProperty("build_duration_compile", "0.6,1.64");

        Mockito.when(workspaceMock.loadPropertiesFile("buildstats")).thenReturn(properties);


        stats.loadStats();
        stats.appendDuration("compile", 221);
        stats.saveStats();

        Properties expectedProperties = (Properties) properties.clone();
        expectedProperties.setProperty("build_duration_compile", "0.6,1.64,0.221");


        Mockito.verify(workspaceMock).writePropertiesFile("buildstats",expectedProperties);
    }

    @Test
    public void givenTenDurationAtLoad_storeDuration_expectTenDurationListWithOldestValueTruncatedAndNewestValueAppended() {
        Properties properties = createInitialProperties();
        properties.setProperty("build_duration_compile", "1,2,3,4,5,6,7,8,9,10");

        Mockito.when(workspaceMock.loadPropertiesFile("buildstats")).thenReturn(properties);


        stats.loadStats();
        stats.appendDuration("compile", 11000);
        stats.saveStats();

        Properties expectedProperties = (Properties) properties.clone();
        expectedProperties.setProperty("build_duration_compile", "2,3,4,5,6,7,8,9,10,11.0");


        Mockito.verify(workspaceMock).writePropertiesFile("buildstats",expectedProperties);
    }

    @Test
    public void setCount_expectNewValueToGetPersisted() {
        Properties properties = createInitialProperties();

        Mockito.when(workspaceMock.loadPropertiesFile("buildstats")).thenReturn(properties);


        stats.loadStats();
        stats.setCount("test", 103);
        stats.saveStats();

        Properties expectedProperties = (Properties) properties.clone();
        expectedProperties.setProperty("test_count", "103");


        Mockito.verify(workspaceMock).writePropertiesFile("buildstats",expectedProperties);
    }

    @Test
    public void givenBlankLoad_getBuildCount_expectZero() {
        Properties properties = new Properties();

        Mockito.when(workspaceMock.loadPropertiesFile("buildstats")).thenReturn(properties);

        stats.loadStats();

        assertEquals( 1, stats.getBuildCount("release") );
    }

    @Test
    public void givenPreviousBuildCount_getBuildCount_expectPreviousValue() {
        Properties properties = new Properties();
        properties.setProperty("buildcount_release","10");

        Mockito.when(workspaceMock.loadPropertiesFile("buildstats")).thenReturn(properties);

        stats.loadStats();

        assertEquals( 10, stats.getBuildCount("release") );
    }

    @Test
    public void givenBlankLoad_incrementBuildCount_expectOneToBeWritten() {
        Properties properties = new Properties();

        Mockito.when(workspaceMock.loadPropertiesFile("buildstats")).thenReturn(properties);

        stats.loadStats();
        stats.incrementBuildCount("release");
        stats.saveStats();

        Properties expectedProperties = (Properties) properties.clone();
        expectedProperties.setProperty("buildcount_release", "1");


        Mockito.verify(workspaceMock).writePropertiesFile("buildstats",expectedProperties);
    }

    @Test
    public void givenPreviousBuildCount_increment_expectIncrementedValueToBeWritten() {
        Properties properties = new Properties();
        properties.setProperty("buildcount_release","3");

        Mockito.when(workspaceMock.loadPropertiesFile("buildstats")).thenReturn(properties);

        stats.loadStats();
        stats.incrementBuildCount("release");
        stats.saveStats();

        Properties expectedProperties = (Properties) properties.clone();
        expectedProperties.setProperty("buildcount_release", "4");


        Mockito.verify(workspaceMock).writePropertiesFile("buildstats",expectedProperties);
    }

    @Test
    public void givenPreviousBuildCount_increment_getBuildCountExpectIncrementedValue() {
        Properties properties = new Properties();
        properties.setProperty("buildcount_release","3");

        Mockito.when(workspaceMock.loadPropertiesFile("buildstats")).thenReturn(properties);

        stats.loadStats();
        stats.incrementBuildCount("release");


        assertEquals( 4, stats.getBuildCount("release") );
    }



    private Properties createInitialProperties() {
        Properties properties = new Properties();
        properties.setProperty("a", "b");
        return properties;
    }
}
