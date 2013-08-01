package com.mosaic.jc.io;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Stack;

/**
 *
 */
public class XMLWriter {
    private static String[] indentStrings = generateIndentStrings();


    private final PrintWriter   out;
    private       int           indentLevel = 1;

    private       Stack<String> openTagNames = new Stack<String>();


    public XMLWriter( Writer out ) {
        this.out = new PrintWriter( out );
    }

    public void printTagWithTextBody( String tagName, String groupId ) {
        printIndent();
        out.print( "<" );
        out.print( tagName );
        out.print( ">" );
        out.print( groupId );
        out.print( "</" );
        out.print( tagName );
        out.println( ">" );
    }

    public void print( String s ) {
        out.print( s );
    }

    public void println( String s ) {
        out.println( s );
    }

    public void println() {
        out.println();
    }

    public void flush() {
        out.flush();
    }

    public void close() {
        out.close();
    }

    private void printIndent() {
        out.print( indentStrings[indentLevel] );
    }


    private static String[] generateIndentStrings() {
        int      numLevels = 20;
        String[] strings   = new String[numLevels];
        String   indentStr = "    ";

        String indentSoFar = "";
        strings[0] = "";

        for ( int i=1; i<numLevels; i++ ) {
            indentSoFar += indentStr;

            strings[i] = indentSoFar;
        }

        return strings;
    }

    public void printStartTags( String...tagNames ) {
        for ( String tagName : tagNames ) {
            printStartTag( tagName );
        }
    }

    public void printOnelineTag( String tagName, boolean flag ) {
        printOnelineTag( tagName, Boolean.toString(flag) );
    }

    public void printOnelineTag( String tagName, String bodyText ) {
        printIndent();

        out.print( '<' );
        out.print( tagName );
        out.print( '>' );
        out.print( bodyText );
        out.print( "</" );
        out.print( tagName );
        out.println( '>' );
    }

    public void printStartTag( String tagName ) {
        printIndent();

        out.print( '<' );
        out.print( tagName );
        out.println( '>' );

        indentLevel++;
        openTagNames.push( tagName );
    }

    public void printEndTag( String tagName ) {
        String expectedTagName = openTagNames.pop();
        if ( !expectedTagName.equals(tagName) ) {
            throw new IllegalStateException( String.format("Expected tag name '%s' but received '%s'",expectedTagName,tagName) );
        }

        indentLevel--;
        printIndent();

        out.print( "</" );
        out.print( tagName );
        out.println( '>' );
    }

    public void printEndTags( String...tagNames ) {
        for ( String tagName : tagNames ) {
            printEndTag( tagName );
        }
    }
}
