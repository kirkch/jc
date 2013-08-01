package com.mosaic.jc.utils;

import com.mosaic.jc.utils.MapUtils;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class MapUtilsTest {

    @Test
    public void toMap_positiveCase() {
        Map expected = new HashMap();
        expected.put( "a", 1 );
        expected.put( "b", 2 );

        assertEquals( expected, MapUtils.toMap("a", 1, "b", 2) );
    }

}
