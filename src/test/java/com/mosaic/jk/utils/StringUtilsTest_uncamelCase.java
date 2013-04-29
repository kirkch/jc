package com.mosaic.jk.utils;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class StringUtilsTest_uncamelCase {

    @Test
    public void singleWordExample() {
        assertEquals( Arrays.asList( "Foo" ), StringUtils.tokeniseCamelCasedString( "foo" ) );
    }

    @Test
    public void twoWordExample() {
        assertEquals( Arrays.asList("Foo","Bar"), StringUtils.tokeniseCamelCasedString( "fooBar" ) );
    }

    @Test
    public void threeWordExample() {
        assertEquals( Arrays.asList("Foo","Bar","Rar"), StringUtils.tokeniseCamelCasedString( "fooBarRar" ) );
    }

    @Test
    public void singleLetterWord() {
        assertEquals( Arrays.asList("F"), StringUtils.tokeniseCamelCasedString( "f" ) );
    }

    @Test
    public void firstWordIsTwoLetterLowercasedAcronym() {
        assertEquals( Arrays.asList("CP"), StringUtils.tokeniseCamelCasedString( "cp" ) );
    }

    @Test
    public void firstWordIsTwoLetterUppercasedAcronym() {
        assertEquals( Arrays.asList("CP"), StringUtils.tokeniseCamelCasedString( "CP" ) );
    }

    @Test
    public void secondWordIsThreeLetterAcronym() {
        assertEquals( Arrays.asList("Hello","CPK"), StringUtils.tokeniseCamelCasedString( "helloCPK" ) );
    }

    @Test
    public void secondWordIsOneLetterAcronym() {
        assertEquals( Arrays.asList("Hello","C"), StringUtils.tokeniseCamelCasedString( "helloC" ) );
    }

    @Test
    public void defect1_wordWhereOnlyVowelIsUpperCased() {
        assertEquals( Arrays.asList("Single","Source","And","Test","Directories"), StringUtils.tokeniseCamelCasedString( "singleSourceAndTestDirectories" ) );
    }

}
