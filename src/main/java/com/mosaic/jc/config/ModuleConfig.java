package com.mosaic.jc.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ModuleConfig {

    public String   moduleNameNbl;

    public String[] mainFQNs;
    public File[]   sourceDirectories;
    public File[]   testDirectories;

    /**
     * Package as in: JAR, WAR etc
     */
    public String packageType;


    /**
     * Name of the jar files that the source files in this module are to
     * be compiled/copied into.
     */
    public List<String> packageInJarNames = new ArrayList<String>();

    public List<Dependency> dependencies = new ArrayList<Dependency>();

}
