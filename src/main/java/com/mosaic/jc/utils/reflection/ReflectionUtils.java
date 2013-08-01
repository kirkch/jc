package com.mosaic.jc.utils.reflection;

import com.mosaic.jc.utils.ArrayUtils;
import com.mosaic.jc.utils.ArrayUtils;
import com.mosaic.jc.utils.Function1;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 */
@SuppressWarnings("unchecked")
public class ReflectionUtils {

    /**
     * Returns true if a is the same as or child of b.
     */
    public static boolean doesAExtendB( Class<?> a, Class<?> b ) {
        return b.isAssignableFrom(a);
    }

    public static <T> T newInstance( Class<T> c ) {
        try {
            return c.newInstance();
        } catch (InstantiationException e) {
            throw new ReflectionException(e);
        } catch (IllegalAccessException e) {
            throw new ReflectionException(e);
        }
    }

    public static <T> T newInstance( Class<T> c, Object...args ) {
        try {
            Class[] parameterTypes = ArrayUtils.map(args, new Function1<Object, Class>() {
                public Class invoke(Object v) {
                    return v == null ? null : v.getClass();
                }
            });

            Constructor constructor = c.getConstructor(parameterTypes);

            return (T) constructor.newInstance(args);
        } catch (InstantiationException e) {
            throw new ReflectionException(e);
        } catch (IllegalAccessException e) {
            throw new ReflectionException(e);
        } catch (NoSuchMethodException e) {
            throw new ReflectionException(e);
        } catch (InvocationTargetException e) {
            throw new ReflectionException(e);
        }
    }

    public static Method getMethod( Class c, String methodName, Class...parameterTypes ) {
        try {
            return c.getMethod( methodName, parameterTypes );
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}
