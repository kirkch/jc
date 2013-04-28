package com.mosaic.jk;

import com.mosaic.jk.env.EnvironmentFake;
import com.mosaic.jk.env.EnvironmentFake.Event;
import com.mosaic.jk.maven.POMWriter;
import com.mosaic.jk.utils.FileUtils;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class Main_InvalidConfigTest {

    private EnvironmentFake env  = new EnvironmentFake();
    private Main            main = new Main(env);
    private POMWriter       out  = Mockito.mock( POMWriter.class );


    @Test
    public void givenMissingWorkingDirectory_expectError() throws IOException {
        File missingDirectory = FileUtils.createTempDirectory();
        main.setRootDirectory( missingDirectory );

        missingDirectory.delete();

        main.generateMavenPOM( out );

        List<EnvironmentFake.Event> expectedEvents = Arrays.asList(
            new Event("started"),
            new Event("ERROR: specified root directory '"+missingDirectory.getPath()+"' does not exist"),
            new Event("finished")
        );

        assertEquals( expectedEvents, env.recordedEvents );
        Mockito.verifyZeroInteractions( out );
    }

}
