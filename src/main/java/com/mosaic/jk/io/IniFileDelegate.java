package com.mosaic.jk.io;

/**
 * Callback interface for IniFileReader.
 */
public interface IniFileDelegate {

    public void parsingStarted();

    /**
     *
     * @param label the trimmed text between the square brackets
     */
    public void labelRead( String label );

    /**
     *
     * @param line non-blank trimmed line
     */
    public void lineRead( String line );


    public void parsingFinished();

}
