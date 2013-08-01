package com.mosaic.jc.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class Config {

    public String groupId;

    /**
     * Suitable for display as a title in english text.  That is it contains
     * spaces and is capitalised.  eg 'Full Spectrum'.
     */
    public String projectName;
    public String versionFull;       // 0.0.1-release_6
    public String version;           // 0.0.1

    public String javaVersion;

    public File rootDirectory;
    public File destinationDirectory;


    public boolean supportsSnapshots;


    public List<ModuleConfig>  modules;

    public List<RepositoryRef> downloadRepositories;



    public List<String> allMainFQNs() {
        List<String> fqns = new ArrayList<String>();

        for ( ModuleConfig module : modules ) {
            Collections.addAll(fqns, module.mainFQNs);
        }

        return fqns;
    }
}
