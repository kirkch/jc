package com.mosaic.jk.config;

import java.io.File;
import java.util.List;

/**
 *
 */
public class ModuleConfig {

    public String   moduleNameNbl;

    public String[] mainFQNs;
    public File[]   sourceDirectories;
    public File[]   testDirectories;

    public String packageAs;


    public List<Dependency> dependencies;


}
