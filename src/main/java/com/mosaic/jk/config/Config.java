package com.mosaic.jk.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class Config {

    public String groupId;
    public String projectName;
    public String versionNumber;       // x.y.z-T      release-10.7.1.229     ck-10.7.1.2     build-(ver).(count)    (ver)-(attr)    10.7.1.229-release


    public File rootDirectory;
    public File destinationDirectory;

    public List<ModuleConfig> modules;



    public List<String> allMainFQNs() {
        List<String> fqns = new ArrayList<String>();

        for ( ModuleConfig module : modules ) {
            Collections.addAll(fqns, module.mainFQNs);
        }

        return fqns;
    }
}
