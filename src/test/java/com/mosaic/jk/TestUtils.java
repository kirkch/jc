package com.mosaic.jk;

import java.io.File;
import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;

/**
 *
 */
public class TestUtils {

    public static File projectRoot() {
        File workingDirectory = new File( System.getProperty("user.dir") );
        File tagFile          = new File( workingDirectory, "JK_ROOT" );

        if ( !tagFile.exists() ) {
            throw new IllegalStateException( "Failed to find the root of the project" );  // todo make the scan more sophisticated (and reliable)
        }

        return workingDirectory;
    }

    public static File examplesDir( String projectName ) {
        return new File( examplesDir(), projectName );
    }

    public static File examplesDir() {
        return new File( projectRoot(), "examples" );
    }

    public static <T extends Comparable> void assertArrayEqualsIgnoreOrder( T[] a, T[] b) {
        Arrays.sort( a );
        Arrays.sort(b);

        assertArrayEquals( a, b );
    }

}
