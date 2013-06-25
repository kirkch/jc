package com.mosaic.jk.config;

import com.mosaic.jk.TestUtils;
import com.mosaic.jk.env.Environment;
import com.mosaic.jk.env.EnvironmentFake;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 *
 */
public class ConfigLoaderTests_repositories {

    private Environment  env          = new EnvironmentFake();
    private ConfigLoader configLoader = new ConfigLoader(env);
    private File         projectDir   = TestUtils.examplesDir("repos/all");
    private Config       config       = configLoader.loadConfigFor(projectDir);


    @Test
    public void defaultGroupId() {
        List<RepositoryRef> expectedRefs = Arrays.asList(
                new RepositoryRef("My Release Repo", "http://nexus.mycompany.com/repository"),
                new RepositoryRef("nexus.sonatype.com Repository", "http://nexus.sonatype.com/repository"),
                new RepositoryRef("Akka Repo", "http://akka.io/repository")
        );

        assertEquals(expectedRefs, config.downloadRepositories);
    }

}
