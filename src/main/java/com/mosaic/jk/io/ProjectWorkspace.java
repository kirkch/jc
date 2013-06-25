package com.mosaic.jk.io;

import com.mosaic.jk.config.RepositoryRef;
import com.mosaic.jk.utils.Function1;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 *
 */
public interface ProjectWorkspace {
    public File[] scanForSourceDirectories();

    public File[] scanForTestDirectories( String moduleNameNbl );

    public boolean hasDependenciesFile();

    public void loadIniFile( String dependencies, IniFileDelegate iniFileDelegate ) throws IOException;

    public Properties loadPropertiesFile(String name);

    public List<RepositoryRef> loadRepositories();
}
