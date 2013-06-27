package com.mosaic.jk.io;

import com.mosaic.jk.config.RepositoryRef;
import com.mosaic.jk.config.RepositoryRefParser;
import com.mosaic.jk.utils.FileUtils;
import com.mosaic.jk.utils.Function1;
import com.mosaic.jk.utils.SetUtils;
import com.mosaic.jk.utils.StringUtils;

import java.io.*;
import java.util.*;

/**
 *
 */
@SuppressWarnings("unchecked")
public class ProjectWorkspaceImpl implements ProjectWorkspace {

    private static final FilenameFilter BASEPACKAGENAME_FILEMATCHER = new FilenameFilter() {
        private Set<String> STANDARD_BASE_NAMES = SetUtils.asSet( "com", "net", "org" );

        @Override
        public boolean accept( File dir, String name ) {
            return name.length() == 2 || STANDARD_BASE_NAMES.contains(name);
        }
    };

    private File rootDir;
    private File projectDir;
    private File dependenciesFile;
    private File repositoriesFile;

    public ProjectWorkspaceImpl( File rootDir ) {
        this.rootDir          = rootDir;
        this.projectDir       = new File(rootDir,"project");
        this.dependenciesFile = new File(projectDir,"dependencies");
        this.repositoriesFile = new File(projectDir,"repositories");
    }

    public File getRootProjectDirectory() {
        return rootDir;
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

    public Properties loadPropertiesFile( String fileName) {
        Properties props = new Properties();
        File file = new File(projectDir, fileName);

        if ( !file.exists() ) {
            return props;
        }

        try {
            props.load( new FileInputStream(file) );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return props;
    }

    public void writePropertiesFile(String fileName, Properties properties) {
        try {
            properties.store( new FileWriter(new File(projectDir,fileName)), "");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<RepositoryRef> loadRepositories() {
        Function1<String,RepositoryRef> lineParser = new Function1<String, RepositoryRef>() {
            private RepositoryRefParser p = new RepositoryRefParser();

            public RepositoryRef invoke(String line) {
                return p.parseRef(line);
            }
        };

        try {
            return loadTextFile( repositoriesFile, Collections.EMPTY_LIST, lineParser );
        } catch ( IOException e ) {
            throw new RuntimeException(e);
        }
    }


    private <T> List<T> loadTextFile( File file, List<T> defaultResults, Function1<String, T> lineParser ) throws FileNotFoundException {
        if ( !file.exists() ) {
            return defaultResults;
        }

        LineNumberReader reader = new LineNumberReader(new FileReader(file));
        try {
            List<T> results = new ArrayList<T>();
            String nextLine = reader.readLine();

            while ( nextLine != null ) {
                if ( !StringUtils.isBlank(nextLine) ) {
                    T result = lineParser.invoke(nextLine);

                    if ( result != null ) {
                        results.add(result);
                    }
                }

                nextLine = reader.readLine();
            }

            return results;
        } catch (IOException e) {
            throw new RuntimeException(e);
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
