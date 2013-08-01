package com.mosaic.jc.utils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class StringUtils {

    public static String capitalizeEachWord( String string ) {
        StringBuilder buf = new StringBuilder(string.length());

        boolean appendSeparator = false;
        for ( String word : string.split(" ") ) {
            if ( appendSeparator ) {
                buf.append(" ");
            } else {
                appendSeparator = true;
            }

            buf.append( capitalize(word) );
        }

        return buf.toString();
    }

    private static String capitalize(String word) {
        return uppercaseCamelcasedToken(word);
    }

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

    public static boolean isEqualTo(String a, String b) {
        if ( a == b ) {
            return true;
        } else if ( a == null ) {
            return false;
        }

        return a.equals(b);
    }

    public static String trimFrom(String line, char c) {
        int index = line.indexOf(c);
        if ( index < 0 ) {
            return line;
        }

        return line.substring(0,index);
    }

}
