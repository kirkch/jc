package com.mosaic.jk.io;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 */
public class ProjectWorkspaceImpl implements ProjectWorkspace {

    private File rootDir;

    public ProjectWorkspaceImpl( File rootDir ) {
        this.rootDir = rootDir;
    }

    public String scanForMainClassFQN() {
        File f = scanForFile( "Main.java" );
        if ( f == null ) {
            return null;
        }

        File sourceDirectory = getSourceDirectory();

        try {
            String srcPath  = sourceDirectory.getCanonicalPath();
            String mainPath = f.getCanonicalPath();

            String relativeMainPath = mainPath.substring(srcPath.length()+1, mainPath.length()-5);

            return relativeMainPath.replaceAll( "/", "." );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File scanForFile( String targetFileName ) {
        Queue<File> queue = new LinkedList<File>();
        queue.add( getSourceDirectory() );

        while ( !queue.isEmpty() ) {
            File candidate = queue.remove();

            if ( candidate.isDirectory() ) {
                File[] a = candidate.listFiles();

                if ( a != null ) {
                    queue.addAll( Arrays.asList(a) );
                }
            } else {
                if ( candidate.getName().equals(targetFileName) ) {
                    return candidate;
                }
            }
        }

        return null;
    }

    private File getSourceDirectory() {
        return new File(rootDir,"src");
    }

}
