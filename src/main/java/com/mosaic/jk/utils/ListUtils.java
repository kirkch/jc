package com.mosaic.jk.utils;

import java.util.List;

/**
 *
 */
public class ListUtils {

    public static <T> String concat( List<T> list, String seperator ) {
        StringBuilder buf = new StringBuilder();

        boolean isSeperatorNeeded = false;
        for ( T v : list ) {
            if ( isSeperatorNeeded ) {
                buf.append( seperator );
            } else {
                isSeperatorNeeded = true;
            }

            buf.append( v == null ? "null" : v.toString() );
        }

        return buf.toString();
    }

}
