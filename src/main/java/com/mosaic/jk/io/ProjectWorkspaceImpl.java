package com.mosaic.jk.io;

import com.mosaic.jk.utils.FileUtils;
import com.mosaic.jk.utils.SetUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 *
 */
public class ProjectWorkspaceImpl implements ProjectWorkspace {

    private static final FilenameFilter BASEPACKAGENAME_FILEMATCHER = new FilenameFilter() {
        private Set<String> STANDARD_BASE_NAMES = SetUtils.asSet( "com", "net", "org" );

        @Override
        public boolean accept( File dir, String name ) {
            return name.length() == 2 || STANDARD_BASE_NAMES.contains(name);
        }
    };

    private File rootDir;
    private File dependenciesFile;

    public ProjectWorkspaceImpl( File rootDir ) {
        this.rootDir          = rootDir;
        this.dependenciesFile = new File(rootDir,"project/dependencies");
    }

    public File[] scanForSourceDirectories() {
        File sourceDirectory = getSourceDirectory();

        return scanForRootSourceDirectories( sourceDirectory );
    }

    public File[] scanForTestDirectories(  String moduleNameNbl  ) {
        if ( moduleNameNbl == null ) {
            File testDir = new File(rootDir,"tests");

            return scanForRootSourceDirectories( testDir );
        } else {
            File testDir = new File(rootDir,"tests/"+moduleNameNbl);
            if ( testDir.exists() && scanForRootSourceDirectories(testDir).length > 0 ) {
                return new File[] {testDir};
            } else {
                return new File[] {};
            }
        }
    }

    public boolean hasDependenciesFile() {
        return dependenciesFile.exists();
    }

    public void loadIniFile( String dependencies, IniFileDelegate iniFileDelegate ) throws IOException {
        IniFileParser parser = new IniFileParser();
        Reader        in     = new FileReader(dependenciesFile);

        try {
            parser.read( in, iniFileDelegate );
        } finally {
            in.close();
        }
    }





    private File[] scanForRootSourceDirectories( File dir ) {
        if ( FileUtils.directoryContains( dir, BASEPACKAGENAME_FILEMATCHER ) ) {
            return new File[] {dir};
        } else {
            return FileUtils.allChildDirectories( dir );
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
