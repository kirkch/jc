package com.mosaic.jk.utils;

import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class SetUtils {

    public static <T> Set<T> asSet( T...elements ) {
        Set<T> set = new HashSet<T>();

        for ( T e : elements ) {
            set.add( e );
        }

        return set;
    }

}
