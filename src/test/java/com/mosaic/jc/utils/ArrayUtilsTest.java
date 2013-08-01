package com.mosaic.jc.utils;

import com.mosaic.jc.utils.ArrayUtils;
import com.mosaic.jc.utils.Function1;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;

/**
 *
 */
public class ArrayUtilsTest {

    private Function1<Integer, String> INT2STRING_FUNCTION = new Function1<Integer, String>() {
        public String invoke(Integer v) {
            return v.toString();
        }
    };

    @Test
    public void map_convertIntsToStrings() {
        assertNull(ArrayUtils.map(null, INT2STRING_FUNCTION));

        String[] result = ArrayUtils.map(new Integer[]{1, 2, 3, 4}, INT2STRING_FUNCTION);
        assertArrayEquals(new String[]{"1", "2", "3", "4"}, result);
    }

}
