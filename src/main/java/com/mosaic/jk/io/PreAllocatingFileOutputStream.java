package com.mosaic.jk.io;

import com.mosaic.jk.utils.Validate;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

/**
 * Optimised FileOutputStream.  Stores all writes in memory until closed, when
 * it will pre-allocate the file in one hit and then stream the data out in
 * one go.  This is premised on the observation that resizing files on the
 * fly is a very costly operation.
 */
public class PreAllocatingFileOutputStream extends OutputStream {
    private static final int DEFAULT_BUF_SIZE_BYTES = 4*1026;


    private File destinationFile;

    private ByteBlock head;
    private ByteBlock activeWriteBlock;

    private int byteCount = 0;


    public PreAllocatingFileOutputStream( File f ) {
        this( f, DEFAULT_BUF_SIZE_BYTES );
    }


    public PreAllocatingFileOutputStream( File f, int bufferSize ) {
        Validate.notNull( f, "destinationFile" );

        this.destinationFile = f;
        this.head            = new ByteBlock( bufferSize );
        activeWriteBlock     = head;
    }

    public void write( int b ) throws IOException {
        activeWriteBlock = activeWriteBlock.write(b);

        byteCount++;
    }

    public void write( byte[] b ) throws IOException {
        activeWriteBlock = activeWriteBlock.write(b);

        byteCount += b.length;
    }

    public void write( byte[] b, int off, int len ) throws IOException {
        activeWriteBlock = activeWriteBlock.write(b, off, len);

        byteCount += len;
    }

    public void flush() throws IOException {}

    public void close() throws IOException {
        destinationFile.mkdirs();

        RandomAccessFile f = new RandomAccessFile(destinationFile, "rw");
        try {
            f.setLength(byteCount);

            ByteBlock b = head;
            while ( b != null ) {
                b.writeTo(f);

                b = b.nextBlock;
            }
        } finally {
            f.close();
        }
    }



    private class ByteBlock {
        private byte[] buffer;
        private int    endIndexExc = 0;

        ByteBlock nextBlock;

        public ByteBlock(int bufferSize) {
            this.buffer = new byte[bufferSize];
        }


        public ByteBlock write( int b ) throws IOException {
            if ( hasCapacity() ) {
                buffer[endIndexExc++] = (byte) b;
                return this;
            } else {
                return nextBlock().write(b);
            }
        }

        public boolean hasCapacity() {
            return endIndexExc < buffer.length-1;
        }

        public boolean isFull() {
            return endIndexExc >= buffer.length;
        }

        public ByteBlock write( byte[] inputBuffer ) throws IOException {
            return this.write( inputBuffer, 0, inputBuffer.length );
        }

        public ByteBlock write( byte[] inputBuffer, int off, int len ) throws IOException {
            int capacity = buffer.length - endIndexExc;

            if ( capacity >= len ) {
                System.arraycopy( buffer, off, inputBuffer, endIndexExc, len );

                return this;
            } else {
                System.arraycopy( buffer, off, inputBuffer, endIndexExc, capacity );

                return nextBlock().write(inputBuffer, capacity, len-capacity);
            }
        }

        public void writeTo(RandomAccessFile f) throws IOException {
            f.write( buffer, 0, endIndexExc );
        }

        private ByteBlock nextBlock() {
            if ( nextBlock == null ) {
                nextBlock = new ByteBlock(buffer.length);
            }

            return nextBlock;
        }
    }
}
