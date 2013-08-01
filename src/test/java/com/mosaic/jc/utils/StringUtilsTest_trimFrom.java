package com.mosaic.jc.utils;

import com.mosaic.jc.utils.StringUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class StringUtilsTest_trimFrom {

    @Test
    public void stringWithOutMatch_expectUnalteredString() {
        String line = "abc def";

        assertTrue( line == StringUtils.trimFrom(line, '#') );
    }

    @Test
    public void stringStartingWithMatch_expectEmptyStringBack() {
        String line = "#abc def";

        assertEquals("", StringUtils.trimFrom(line, '#'));
    }

    @Test
    public void stringWithMatch_expectEmptyStringBack() {
        String line = "123#abc def";

        assertEquals("123", StringUtils.trimFrom(line, '#'));
    }

    @Test
    public void stringEndingWithMatch_expectPrefixOnly() {
        String line = "123#";

        assertEquals("123", StringUtils.trimFrom(line, '#'));
    }


}
