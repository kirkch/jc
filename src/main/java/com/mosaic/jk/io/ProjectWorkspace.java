package com.mosaic.jk.io;

import java.io.File;

/**
 *
 */
public interface ProjectWorkspace {
    public File[] scanForSourceDirectories();

    public File[] scanForTestDirectories();
}
