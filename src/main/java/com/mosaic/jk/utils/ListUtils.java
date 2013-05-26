package com.mosaic.jk.utils;

import com.mosaic.jk.config.ModuleConfig;

import java.util.ArrayList;
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

    public static <A,B> List<B> map( List<A> list, Function1<A,B> mappingFunction ) {
        List<B> results = new ArrayList<B>( list.size() );

        for ( A a : list ) {
            results.add( mappingFunction.invoke(a) );
        }

        return results;
    }

    public static <T> T selectFirstMatch( List<T> list, Function1<T, Boolean> predicate ) {
        for ( T v : list ) {
            if ( predicate.invoke(v) ) {
                return v;
            }
        }

        return null;
    }

}
