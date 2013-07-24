package com.mosaic.jk.utils.reflection;

/**
 *
 */
public class ReflectionUtils {

    public static <T> T newInstance( Class<T> c ) {
        try {
            return c.newInstance();
        } catch (InstantiationException e) {
            throw new ReflectionException(e);
        } catch (IllegalAccessException e) {
            throw new ReflectionException(e);
        }
    }

}
