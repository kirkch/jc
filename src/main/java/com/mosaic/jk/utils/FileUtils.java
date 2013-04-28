package com.mosaic.jk.utils;

import java.io.File;

/**
 *
 */
public class FileUtils {

    public static File createTempDirectory() {
        File rootDirectory = new File( System.getProperty("java.io.tmpdir") );

        File newDirectory;
        long num = System.currentTimeMillis();
        do {
            num++;
            newDirectory = new File(rootDirectory,Long.toString(num));
        } while ( newDirectory.exists() );

        return newDirectory;
    }

    public static File getWorkingDirectory() {
        return new File( System.getProperty("user.dir") );
    }
}
