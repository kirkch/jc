package com.mosaic.jc.io;

import com.mosaic.jc.utils.StringUtils;
import com.mosaic.jc.utils.StringUtils;
import com.mosaic.jc.utils.Validate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Loads ini files. This reader defines ini files as having lines with two variations of format:
 *
 * <pre>
 * [LABEL NAME]
 * Non-blank text on a line
 * </pre>
 */
public class IniFileParser {

    public void read( Reader in, IniFileDelegate delegate ) throws IOException {
        Validate.notNull( in,       "inReader" );
        Validate.notNull( delegate, "delegate" );

        delegate.parsingStarted();

        try {
            BufferedReader i = new BufferedReader( in );

            String line = i.readLine();
            while ( line != null ) {
                parseLine( line, delegate );

                line = i.readLine();
            }
        } finally {
            delegate.parsingFinished();
        }
    }

    private void parseLine( String line, IniFileDelegate delegate ) {
        String trimmedLine = stripCommentFrom(line).trim();

        if ( trimmedLine.startsWith("[") && trimmedLine.endsWith("]") ) {
            String label = trimmedLine.substring( 1, trimmedLine.length()-1 ).trim();

            delegate.labelRead( label );
        } else if ( trimmedLine.length() > 0 ) {
            delegate.lineRead(trimmedLine);
        }
    }

    private String stripCommentFrom(String line) {
        return StringUtils.trimFrom(line, '#');
    }

}
