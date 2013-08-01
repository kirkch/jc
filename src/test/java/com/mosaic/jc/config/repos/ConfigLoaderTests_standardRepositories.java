package com.mosaic.jc.config.repos;

import com.mosaic.jc.config.BaseConfigTestCase;
import com.mosaic.jc.config.RepositoryRef;
import org.junit.Test;

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
