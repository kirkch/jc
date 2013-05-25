package com.mosaic.jk.utils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class StringUtils {


    public static List<String> tokeniseCamelCasedString( String s ) {
        List<String> list          = new ArrayList<String>();
        int          numCharacters = s.length();

        int startOfWordBoundaryInc = 0;
        while ( startOfWordBoundaryInc<numCharacters ) {
            int endOfWordBoundaryExc = detectEndOfCamelCasedWord( s, startOfWordBoundaryInc, numCharacters );

            String substring = s.substring( startOfWordBoundaryInc, endOfWordBoundaryExc );
            substring = uppercaseCamelcasedToken( substring );

            list.add( substring );

            startOfWordBoundaryInc = endOfWordBoundaryExc;
        }

        return list;
    }

    private static String uppercaseCamelcasedToken( String substring ) {
        switch ( substring.length() ) {
            case 0: return substring;
            case 1: return substring.toUpperCase();
            default:
                int count = countVowels(substring);

                if ( count == 0 ) {
                    return substring.toUpperCase();
                } else {
                    return Character.toUpperCase(substring.charAt(0)) + substring.substring(1);
                }
        }
    }

    private static int countVowels( String s ) {
        int l     = s.length();
        int count = 0;

        for ( int i=0; i<l; i++ ) {
            switch ( s.charAt(i) ) {
                case 'a':
                case 'e':
                case 'i':
                case 'o':
                case 'u':
                case 'A':
                case 'E':
                case 'I':
                case 'O':
                case 'U':
                    count++;
                    break;
                default:
            }
        }

        return count;
    }

    private static int detectEndOfCamelCasedWord( String s, int startOfWordBoundaryInc, int endOfStringBoundaryExc ) {
        if ( startOfWordBoundaryInc+1 == endOfStringBoundaryExc ) {
            return startOfWordBoundaryInc+1;   // single letter edge case
        } else if ( Character.isUpperCase(s.charAt(startOfWordBoundaryInc)) && Character.isUpperCase(s.charAt(startOfWordBoundaryInc+1))) {
            return detectEndOfUpperCasedSequenceStartingFrom( s, startOfWordBoundaryInc, endOfStringBoundaryExc ); // acronym edge case
        }

        for ( int i=startOfWordBoundaryInc+1; i<endOfStringBoundaryExc; i++ ) {
            char c = s.charAt( i );

            if ( Character.isUpperCase(c) ) {
                return i;
            }
        }

        return endOfStringBoundaryExc;
    }

    private static int detectEndOfUpperCasedSequenceStartingFrom( String s, int startOfWordBoundaryInc, int endOfStringBoundaryExc ) {
        for ( int i=startOfWordBoundaryInc+1; i<endOfStringBoundaryExc; i++ ) {
            char c = s.charAt(i);

            if ( !Character.isUpperCase(c) ) {
                return i;
            }
        }

        return endOfStringBoundaryExc;
    }

    public static void trimArray( String[] array ) {
        for ( int i=0; i<array.length; i++ ) {
            array[i] = array[i].trim();
        }
    }

    public static boolean isBlank(String str) {
        return str == null || str.trim().length() == 0;
    }

}
