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


    public List<Dependency> dependencies = new ArrayList<Dependency>();

}
