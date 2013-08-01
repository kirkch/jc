package com.mosaic.jc.config;

import com.mosaic.jc.config.ArtifactParser;
import org.junit.Test;

import static junit.framework.Assert.assertNull;

/**
 *
 */
public class ArtifactParserTest {

    private ArtifactParser parser = new ArtifactParser();


    @Test
    public void givenNull_expectNull() {
        assertNull( parser.invoke(null) );
    }

    @Test
    public void givenEmptyString_expectNull() {
        assertNull( parser.invoke("") );
    }

    @Test
    public void givenBlankString_expectNull() {
        assertNull( parser.invoke("    \t") );
    }

    @Test
    public void givenCommentLine_expectNull() {
        assertNull( parser.invoke("# foo bar") );
    }

    @Test
    public void givenCommentLinePrefixedWithWhitespace_expectNull() {
        assertNull( parser.invoke("    \t# foo bar") );
    }

    // givenWellFormedSingleModuleLine_expectSuccessfulParse
    // givenWellFormedMultiModuleLine_expectSuccessfulParse
    // givenEmptyModuleSection_expect
}
