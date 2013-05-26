package com.mosaic.jk.io;

import java.io.File;
import java.io.IOException;

/**
 *
 */
public interface ProjectWorkspace {
    public File[] scanForSourceDirectories();

    public File[] scanForTestDirectories( String moduleNameNbl );

    public boolean hasDependenciesFile();

    public void loadIniFile( String dependencies, IniFileDelegate iniFileDelegate ) throws IOException;
}
