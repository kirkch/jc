package com.mosaic.jk.config;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class RepositoryRefParserTest {

    private RepositoryRefParser parser = new RepositoryRefParser();

    @Test
    public void urlOnly() {
        String url = "http://myrepo.mycompany.com/repository";
        RepositoryRef ref = parser.parseRef(url);

        assertEquals( url, ref.url );
    }


    // urlAndRepositoryName
    //invalidURL_expectException


}
