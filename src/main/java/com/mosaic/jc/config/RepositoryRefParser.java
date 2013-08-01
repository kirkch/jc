package com.mosaic.jc.config;

import com.mosaic.jc.utils.StringUtils;
import com.mosaic.jc.utils.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 */
public class RepositoryRefParser {

    public RepositoryRef parseRef( String ref ) {
        String[] tokens = splitRef(ref);

        return new RepositoryRef(tokens[0], tokens[1]);
    }

    private String[] splitRef(String ref) {
        String[] tokens = new String[2];

        int index = getNameUrlSeparatorIndex(ref);
        if ( index < 0 ) {
            tokens[1] = ref.trim();
            tokens[0] = extractDefaultNameFromUrl(tokens[1]);
        } else {
            tokens[0] = ref.substring(0,index).trim();
            tokens[1] = ref.substring(index+1).trim();
        }

        return tokens;
    }

    private String extractDefaultNameFromUrl( String urlString ) {
        try {
            URL url = new URL(urlString);

            String headFragment = url.getHost();
            String tailFragment = url.getPath().replaceAll("/"," ");

            return headFragment + StringUtils.capitalizeEachWord(tailFragment);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private int getNameUrlSeparatorIndex(String ref) {
        int i = ref.indexOf(':');
        if ( i < 0 ) {
            return i;
        } else if ( ref.length() > (i+1) && ref.charAt(i+1) != '/' ) {
            return i;
        }

        return -1;
    }

}
