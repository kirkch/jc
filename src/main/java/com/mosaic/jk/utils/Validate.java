package com.mosaic.jk.utils;

/**
 *
 */
public class Validate {

    public static void notNull( Object v, String argName ) {
        if ( v == null ) {
            fail( "'%s' is not nullable", argName );
        }
    }

    public static void isTrue(boolean b, String s) {
        if ( !b ) {
            fail( s );
        }
    }

    private static void fail( String msg, String...argValues ) {
        throw new IllegalArgumentException( String.format(msg,argValues) );
    }
}
