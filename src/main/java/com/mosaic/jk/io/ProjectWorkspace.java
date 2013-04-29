package com.mosaic.jk.io;

import java.io.File;

/**
 *
 */
public interface ProjectWorkspace {
    public String[] scanForMainClassFQNs();

    public File[] scanForSourceDirectories();

    public File[] scanForTestDirectories();
}
