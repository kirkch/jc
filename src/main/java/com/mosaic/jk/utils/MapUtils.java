package com.mosaic.jk.utils;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class MapUtils {

    public static Map toMap( Object...args ) {
        Validate.isTrue(args.length % 2 == 0, "toMap(..) expects an even number of arguments");

        Map map = new HashMap();

        for ( int i=0; i<args.length; i+=2 ) {
            map.put( args[i], args[i+1] );
        }

        return map;
    }

}
