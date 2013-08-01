package com.mosaic.jc.utils.reflection;

import com.mosaic.jc.utils.reflection.ReflectionUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

/**
 *
 */
public class ReflectionUtilsTest {

    @Test
    public void doesAExtendB() {
        // concrete classes
        assertTrue( ReflectionUtils.doesAExtendB(String.class, String.class) );
        assertTrue( ReflectionUtils.doesAExtendB(String.class, Object.class) );
        assertFalse(ReflectionUtils.doesAExtendB(Object.class, String.class));

        // class implements interface
        assertTrue(ReflectionUtils.doesAExtendB(ArrayList.class, List.class));
        assertFalse(ReflectionUtils.doesAExtendB(List.class, ArrayList.class));

        // interface extends interface
        assertTrue(ReflectionUtils.doesAExtendB(List.class, List.class));
        assertTrue(ReflectionUtils.doesAExtendB(List.class, Collection.class));
        assertFalse(ReflectionUtils.doesAExtendB(Collection.class, List.class));
    }

    @Test
    public void newInstance_noArgs() {
        ReflectionUtilsTest instance = ReflectionUtils.newInstance(ReflectionUtilsTest.class);

        assertNotNull(instance);
    }

    @Test
    public void newInstance_withArgs() {
        assertEquals( Boolean.TRUE, ReflectionUtils.newInstance(Boolean.class,"true") );
    }

}
