package com.mosaic.jk.config;

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
        } else {
            tokens[0] = ref.substring(0,index).trim();
            tokens[1] = ref.substring(index+1).trim();
        }

        return tokens;
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
