package com.mosaic.jk.config;

import java.util.Map;

/**
 *
 */
public class Config {

    public String groupId;
    public String projectName;
    public String version;       // x.y.z-T      release-10.7.1.229     ck-10.7.1.2     build-(ver).(count)    (ver)-(attr)    10.7.1.229-release

    public String mainFQN;


    public Map<String,ModuleConfig> modules;

}
