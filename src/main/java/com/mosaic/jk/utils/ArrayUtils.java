package com.mosaic.jk.utils;

import java.util.Arrays;

/**
 *
 */
public class ArrayUtils {

    public static <T> String concat( T[] elements, String seperator ) {
        return ListUtils.concat( Arrays.asList(elements), seperator );
    }

}
