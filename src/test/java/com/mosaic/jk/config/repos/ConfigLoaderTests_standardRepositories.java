package com.mosaic.jk.config.repos;

import com.mosaic.jk.TestUtils;
import com.mosaic.jk.config.BaseConfigTestCase;
import com.mosaic.jk.config.Config;
import com.mosaic.jk.config.ConfigLoader;
import com.mosaic.jk.config.RepositoryRef;
import com.mosaic.jk.env.Environment;
import com.mosaic.jk.env.EnvironmentFake;
import com.mosaic.jk.io.ProjectWorkspaceImpl;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class ConfigLoaderTests_standardRepositories extends BaseConfigTestCase {

    public ConfigLoaderTests_standardRepositories() {
        super("repos/standard");
    }


    @Test
    public void defaultGroupId() {
        List<RepositoryRef> expectedRefs = Arrays.asList(
                new RepositoryRef("My Release Repo", "http://nexus.mycompany.com/repository"),
                new RepositoryRef("nexus.sonatype.com Repository", "http://nexus.sonatype.com/repository"),
                new RepositoryRef("Akka Repo", "http://repo.akka.io/releases")
        );

        assertEquals(expectedRefs, config.downloadRepositories);
    }

}
