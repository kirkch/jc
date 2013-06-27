package com.mosaic.jk.env;

import com.mosaic.jk.io.ProjectWorkspace;
import org.apache.commons.lang3.StringUtils;

import java.util.Properties;

/**
 * Captures statistics about the build runs.  Stores data in 'project/buildstats'
 * between runs.
 */
public class BuildStats {

/*
buildcount_username=10

build_duration_clean=0.5,0.7,0.2    #up to ten
build_duration_pomwriter=
build_duration_compile=
build_duration_package=
build_duration_test=
build_duration_total=

test_count=102
javafile_count=80
scalafile_count=80
*/

    private ProjectWorkspace workspace;
    private Properties       properties = new Properties();


    public BuildStats( ProjectWorkspace workspace ) {
        this.workspace = workspace;
    }

    public void loadStats() {
        properties = workspace.loadPropertiesFile("buildstats");
    }

    public void saveStats() {
        workspace.writePropertiesFile("buildstats", properties);
    }

    public void appendDuration( String phaseName, long durationMillis ) {
        String key               = "build_duration_"+phaseName;
        String oldValue          = properties.getProperty(key, "");
        String formattedDuration = Double.toString(durationMillis/1000.0);

        String newValue = oldValue.isEmpty() ? formattedDuration : (oldValue + "," + formattedDuration);

        newValue = trimCommaSeparatedList( newValue, 10 );

        properties.setProperty(key, newValue);
    }

    public void setCount( String entityName, int count ) {
        properties.setProperty( entityName+"_count", Integer.toString(count) );
    }

    public void incrementBuildCount( String buildCategory ) {
        String key   = "buildcount_"+buildCategory;
        String value = properties.getProperty(key, "0");

        long currentValue = Long.parseLong(value);

        properties.setProperty( key, Long.toString(currentValue+1) );
    }

    public long getBuildCount( String buildCategory ) {
        String key   = "buildcount_"+buildCategory;
        String value = properties.getProperty(key, "0");

        return Long.parseLong(value);
    }

    private String trimCommaSeparatedList( String commaSeparatedString, int maxLength ) {
        int commaCount = StringUtils.countMatches(commaSeparatedString, ",");

        while ( commaCount >= maxLength ) {
            int firstCommaIndex = commaSeparatedString.indexOf(',');
            commaSeparatedString = commaSeparatedString.substring(firstCommaIndex+1);

            commaCount = StringUtils.countMatches(commaSeparatedString,",");
        }

        return commaSeparatedString;
    }

}
