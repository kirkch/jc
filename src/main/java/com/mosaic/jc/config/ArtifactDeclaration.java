package com.mosaic.jc.config;

import java.util.Set;

/**
 *
 */
public class ArtifactDeclaration {
    public String artifactName;
    public String packagingType;   // eg ThinJar, FatJar, SelfDownloadingJar, LibDirectoryJar
    public Set<String> includeModuleNames;
}
