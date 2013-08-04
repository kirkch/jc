package com.mosaic.jc.config;

import com.mosaic.jc.utils.SetUtils;
import org.junit.Ignore;
import org.junit.Test;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;


/**
 *
 */
@Ignore // TODO working here
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

    @Test
    public void givenWellFormedSingleModule_expectSuccessfulParse() {
        ArtifactDeclaration art = parser.invoke("client: jar(m1)");

        ArtifactDeclaration expected = new ArtifactDeclaration(
                "client",
                "jar",
                SetUtils.asSet("m1")
        );

        assertEquals( expected, art );
    }


    // givenWellFormedMultiModuleLine_expectSuccessfulParse
    // givenEmptyModuleSection_expect
}
