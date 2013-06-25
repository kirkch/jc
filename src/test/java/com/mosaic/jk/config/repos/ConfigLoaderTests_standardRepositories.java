package com.mosaic.jk.config.repos;

import com.mosaic.jk.TestUtils;
import com.mosaic.jk.config.Config;
import com.mosaic.jk.config.ConfigLoader;
import com.mosaic.jk.config.RepositoryRef;
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
public class ConfigLoaderTests_standardRepositories {

    private Environment  env          = new EnvironmentFake();
    private ConfigLoader configLoader = new ConfigLoader(env);
    private File         projectDir   = TestUtils.examplesDir("repos/standard");
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
