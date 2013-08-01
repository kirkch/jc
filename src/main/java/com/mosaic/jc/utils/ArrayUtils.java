package com.mosaic.jc.utils;

import com.mosaic.jc.utils.reflection.ReflectionUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 *
 */
@SuppressWarnings("unchecked")
public class ArrayUtils {

    public static <T> String concat( T[] elements, String separator ) {
        return ListUtils.concat( Arrays.asList(elements), separator );
    }

    public static <A,B> B[] map( A[] array, Function1<A, B> function ) {
        if ( array == null ) {
            return null;
        }

        int elementCount = array.length;
        B[] result       = createResultsArray( array.getClass().getComponentType(), function, elementCount );

        for ( int i=0; i<elementCount; i++ ) {
            result[i] = function.invoke( array[i] );
        }

        return result;
    }


    private static <A,B> B[] createResultsArray( Class componentType, Function1<A, B> function, int elementCount ) {
        Method conversionFunction = ReflectionUtils.getMethod( function.getClass(), "invoke", componentType );
        Class  resultType         = conversionFunction.getReturnType();

        return (B[]) Array.newInstance( resultType, elementCount );
    }

}
