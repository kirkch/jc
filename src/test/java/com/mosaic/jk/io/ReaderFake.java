package com.mosaic.jk.io;

import java.io.IOException;
import java.io.Reader;

/**
 * Starts life as a StringReader however it includes functions that allow IOExceptions to be inserted into the conversation.
 * Used for testing.
 */
public class ReaderFake extends Reader {

    private String inputSource;

    /**
     * Points to the index in inputSource where the next read is to start from. Reading ends when the index goes out of bounds
     * of the input source.
     */
    private int currentIndex;

    private boolean errorOnCloseFlag;
    private int     errorAfterNCharactersRead = Integer.MAX_VALUE;

    public ReaderFake( String inputSource ) {
        this.inputSource = inputSource;
    }

    public void errorAfterNCharacters( int numCharacters ) {
        errorAfterNCharactersRead = numCharacters;
    }

    public void errorOnClose( boolean flag ) {
        errorOnCloseFlag = flag;
    }

    @Override
    public int read( char[] cbuf, int cbufWriteOffset, int maxReadCount ) throws IOException {
        int sourceLength = inputSource.length();
        if ( sourceLength == currentIndex ) {
            return -1;
        }

        int readCount = Math.min( (sourceLength - currentIndex), maxReadCount );

        inputSource.getChars( currentIndex, currentIndex+readCount, cbuf, cbufWriteOffset );
        currentIndex += readCount;

        if ( currentIndex > errorAfterNCharactersRead ) {
            throw new IOException( "injected exception" );
        }

        return readCount;
    }

    @Override
    public void close() throws IOException {
        if ( errorOnCloseFlag ) {
            throw new IOException("injected error on close");
        }
    }

}
