package com.mosaic.jk;

import java.io.File;

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
}
