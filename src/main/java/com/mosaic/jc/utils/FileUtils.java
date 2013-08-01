package com.mosaic.jc.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Stack;

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
        try {
            return new File( System.getProperty("user.dir") ).getCanonicalFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static File[] allChildDirectories( File directory ) {
        File[] files = directory.listFiles( DIRECTORY_FILTER );

        return files == null ? new File[0] : files;
    }

    /**
     * Creates a new symbolic link pointing at the target file.  If symbolic
     * links are not supported then the target file will be copied to the link
     * location.
     */
    public static void createSymbolicLink( File link, File target ) throws IOException {
        Validate.isTrue( target.exists(), "target file '"+target+"' does not exist" );

        if ( link.exists() ) {
            link.delete();
        }

        Path linkPath   = Paths.get( link.toURI() );
        Path targetPath = Paths.get( target.toURI() );

        try {
            Files.createSymbolicLink( linkPath, targetPath );
        } catch (IOException e) {
            // IOException from createSymbolicLink means that the file system does not
            // support symbolic links..  so we fall back to performing a file copy

            Files.copy(targetPath, linkPath );
        }
    }


    private static final FileFilter DIRECTORY_FILTER = new FileFilter() {
        @Override
        public boolean accept( File f ) {
            return f.isDirectory();
        }
    };

    public static boolean directoryContains( File dir, FilenameFilter filter ) {
        String[] matches = dir.list( filter );

        return matches != null && matches.length > 0;
    }

    public static String toRelativePath( File parent, File absolutePath ) {
        try {
            String srcPath  = parent.getCanonicalPath();
            String mainPath = absolutePath.getCanonicalPath();

            return mainPath.substring(srcPath.length()+1, mainPath.length());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void depthFirstScan( File startingDirectory, VoidFunction1<File> callback ) {
        Stack<File> stack = new Stack<File>();

        stack.push( startingDirectory );

        do {
            File f = stack.pop();

            callback.invoke( f );

            if ( f.isDirectory() ) {
                for ( File child : f.listFiles() ) {
                    stack.push(child);
                }
            }
        } while ( !stack.isEmpty() );
    }
}
