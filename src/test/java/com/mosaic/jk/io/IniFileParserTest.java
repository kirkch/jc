package com.mosaic.jk.io;

import com.mosaic.jk.utils.ArrayUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *
 */
public class IniFileParserTest {

    private IniFileParser reader   = new IniFileParser();
    private IniFileDelegateFake delegate = new IniFileDelegateFake();



    @Test
    public void givenNullReader_expectException() throws IOException {
        try {
            reader.read( null, delegate );
            fail( "expected exception" );
        } catch ( IllegalArgumentException e ) {
            assertEquals( "'inReader' is not nullable", e.getMessage() );
        }
    }

    @Test
    public void givenNullDelegate_expectException() throws IOException {
        Reader in = new InputStreamReader( this.getClass().getResourceAsStream("empty.ini") );

        try {
            reader.read( in, null );
            fail( "expected exception" );
        } catch ( IllegalArgumentException e ) {
            assertEquals( "'delegate' is not nullable", e.getMessage() );
        } finally {
            in.close();
        }
    }

    @Test
    public void givenEmptyIniFile_expectStartStopCallbacks() throws IOException {
        Reader in = new InputStreamReader( this.getClass().getResourceAsStream("empty.ini") );

        try {
            reader.read( in, delegate );

            List<String> expectedEvents = Arrays.asList( "parsingStarted", "parsingFinished" );

            assertEquals( expectedEvents, delegate.history );
        } finally {
            in.close();
        }
    }

    @Test
    public void givenIniFileWithSingleLabel_expectLabelCallback() throws IOException {
        Reader in = new InputStreamReader( this.getClass().getResourceAsStream("singleLabel.ini") );

        try {
            reader.read( in, delegate );

            List<String> expectedEvents = Arrays.asList( "parsingStarted", "labelRead(LABEL)", "parsingFinished" );

            assertEquals( expectedEvents, delegate.history );
        } finally {
            in.close();
        }
    }

    @Test
    public void givenIniFileWithSingleLabelMixedWithWhitespace_expectLabelCallback() throws IOException {
        Reader in = new InputStreamReader( this.getClass().getResourceAsStream("singleLabelWithWhitespace.ini") );

        try {
            reader.read( in, delegate );

            List<String> expectedEvents = Arrays.asList( "parsingStarted", "labelRead(Noise is   Good)", "parsingFinished" );

            assertEquals( expectedEvents, delegate.history );
        } finally {
            in.close();
        }
    }

    @Test
    public void givenIniFileWithNonBlankLine_expectLineReadCallback() throws IOException {
        Reader in = new InputStreamReader( this.getClass().getResourceAsStream("nonBlankLine.ini") );

        try {
            reader.read( in, delegate );

            List<String> expectedEvents = Arrays.asList( "parsingStarted", "lineRead(Hello World)", "parsingFinished" );

            assertEquals( expectedEvents, delegate.history );
        } finally {
            in.close();
        }
    }

    @Test
    public void givenIniFileWithNonBlankLineMixedWithWhitespace_expectLineReadCallback() throws IOException {
        Reader in = new InputStreamReader( this.getClass().getResourceAsStream("nonBlankLineWithWhitespace.ini") );

        try {
            reader.read( in, delegate );

            List<String> expectedEvents = Arrays.asList( "parsingStarted", "lineRead(Hello  World)", "parsingFinished" );

            assertEquals( expectedEvents, delegate.history );
        } finally {
            in.close();
        }
    }

    @Test
    public void givenWildIniFile_expectCorrectMixOfCallbacksIncludingWhitespaceHandling() throws IOException {
        Reader in = new InputStreamReader( this.getClass().getResourceAsStream("wild.ini") );

        try {
            reader.read( in, delegate );

            List<String> expectedEvents = Arrays.asList(
                "parsingStarted",
                "labelRead(WILD)",
                "lineRead(in the)",
                "lineRead(wild)",
                "labelRead(MIST)",
                "lineRead(it was)",
                "lineRead(cold)",
                "parsingFinished"
            );

            assertEquals( expectedEvents, delegate.history );
        } finally {
            in.close();
        }
    }

    @Test
    public void givenIOExceptionDuringReading_expectParseFailedCallback() throws IOException {
        ReaderFake in = new ReaderFake( "[LABEL]" );
        in.errorAfterNCharacters(2);

        try {
            reader.read( in, delegate );
            fail( "expected exception" );
        } catch ( IOException e ) {
            assertEquals( "injected exception", e.getMessage() );
        } finally {
            in.close();
        }
    }

    @Test
    public void givenIOExceptionWhenClosingTheFile_expectNoErrorEventAsTheParserDoesNotCloseTheReader() throws IOException {
        ReaderFake in = new ReaderFake( "[LABEL]" );
        in.errorOnClose( true );

        try {
            reader.read( in, delegate );
        } finally {
            try {
                in.close();
                fail( "expected exception" );
            } catch ( IOException e ) {
                assertEquals( "injected error on close", e.getMessage() );
            }
        }
    }



    private static class IniFileDelegateFake implements IniFileDelegate {
        public final List<String> history = new ArrayList<String>();

        @Override
        public void parsingStarted() {
            append("parsingStarted");
        }

        @Override
        public void labelRead( String label ) {
            append("labelRead", label);
        }

        @Override
        public void lineRead( String line ) {
            append("lineRead", line);
        }

        @Override
        public void parsingFinished() {
            append("parsingFinished");
        }

        private void append( String methodName, Object...args ) {
            String desc = args.length == 0 ? methodName : (methodName+"("+ ArrayUtils.concat(args,",")+")");
            
            history.add(desc);
        }
    }

}
