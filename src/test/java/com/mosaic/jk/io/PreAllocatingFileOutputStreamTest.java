package com.mosaic.jk.io;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.java.quickcheck.Generator;
import net.java.quickcheck.generator.PrimitiveGenerators;

import java.io.*;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 *
 */
public class PreAllocatingFileOutputStreamTest {
    private static int BUFFER_SIZE = 20;

    private static Generator<String> LB_GENERATOR   = PrimitiveGenerators.strings(BUFFER_SIZE-1);
    private static Generator<String> EB_GENERATOR   = PrimitiveGenerators.strings(BUFFER_SIZE);
    private static Generator<String> GB_GENERATOR   = PrimitiveGenerators.strings(BUFFER_SIZE*3);
    private static Generator<String> GBx2_GENERATOR = PrimitiveGenerators.strings(BUFFER_SIZE*2, BUFFER_SIZE*4);
    private static Generator<String> GBx4_GENERATOR = PrimitiveGenerators.strings(BUFFER_SIZE*4, BUFFER_SIZE*6);

    private File         destFile;
    private OutputStream outputStream;


    @Before
    public void init() throws IOException {
        destFile     = File.createTempFile("PreAllocatingFileOutputStreamTest", ".dat");

        outputStream = new PreAllocatingFileOutputStream(destFile, BUFFER_SIZE);
    }

    @After
    public void tearDown() {
        destFile.delete();
    }


    @Test
    public void writeSingleByte_expectSingleByteInOutputFile() throws IOException {
        outputStream.write('a');
        outputStream.close();

        assertDestFileContains( new byte[] {'a'} );
    }

    @Test
    public void writeByteArrayLessThanBufferSize_expectFileToContainArray() throws IOException {
        byte[] bytes = LB_GENERATOR.next().getBytes();

        outputStream.write(bytes);
        outputStream.close();

        assertDestFileContains( bytes );
    }

    @Test
    public void appendTwoBytesInARow() throws IOException {
        outputStream.write('a');
        outputStream.write('b');
        outputStream.close();

        assertDestFileContains( new byte[] {'a', 'b'} );
    }

    @Test
    public void appendTwoArraysInARow() throws IOException {
        outputStream.write(new byte[] {'a','b'});
        outputStream.write(new byte[] {'c','d'});
        outputStream.close();

        assertDestFileContains( new byte[] {'a', 'b','c','d'} );
    }

    @Test
    public void writePartOfArrayLessThanBufferSize_expectFileToContainSpecifiedBytes() throws IOException {
        outputStream.write(new byte[] {'a','b','c','d','e','f','g'}, 3, 2);
        outputStream.close();

        assertDestFileContains( new byte[] {'d', 'e'} );
    }

    @Test
    public void writeArrayCrossingOverBufferSizeUsing_expectFileToContainSpecifiedBytes() throws IOException {
        byte[] bytes = GB_GENERATOR.next().getBytes();
        outputStream.write(bytes);
        outputStream.close();

        assertDestFileContains( bytes );
    }

    @Test
    public void writePartOfArrayCrossingOverBufferSizeUsing_expectFileToContainSpecifiedBytes() throws IOException {
        byte[] bytes = GBx2_GENERATOR.next().getBytes();

        int offset   = BUFFER_SIZE / 2;
        int numBytes = BUFFER_SIZE+BUFFER_SIZE/2;

        outputStream.write(bytes, offset, numBytes);
        outputStream.close();

        assertDestFileContains(Arrays.copyOfRange(bytes,offset,offset+numBytes) );
    }

    @Test
    public void tryToOverflowInputBuffer_expectException() throws IOException {
        byte[] bytes = GBx2_GENERATOR.next().getBytes();

        int offset   = bytes.length-5;

        try {
            outputStream.write(bytes, offset, 10);
            fail( "expected IllegalArgumentException");
        } catch ( IllegalArgumentException e ) {
            assertEquals( "request to copy over flows right hand side of the input buffer", e.getMessage() );
        }
    }

    @Test
    public void writeArrayExactlyOnBufferSize_expectFileToContainSpecifiedBytes() throws IOException {
        byte[] bytes = EB_GENERATOR.next().getBytes();

        outputStream.write(bytes, 0, bytes.length);
        outputStream.close();

        assertDestFileContains(bytes);
    }

    @Test
    public void writePartOfArrayExactlyOnBufferSize_expectFileToContainSpecifiedBytes() throws IOException {
        byte[] bytes = GBx2_GENERATOR.next().getBytes();

        int offset   = BUFFER_SIZE / 2;
        int numBytes = BUFFER_SIZE;

        outputStream.write(bytes, offset, numBytes);
        outputStream.close();

        assertDestFileContains(Arrays.copyOfRange(bytes,offset,offset+numBytes) );
    }

    @Test
    public void appendMultipleArraysCrossingSeveralBufferSizeBoundaries() throws IOException {
        byte[] bytes = GBx4_GENERATOR.next().getBytes();

        outputStream.write(bytes, 0,             BUFFER_SIZE);
        outputStream.write(bytes, BUFFER_SIZE,   BUFFER_SIZE);
        outputStream.write(bytes, BUFFER_SIZE*2, BUFFER_SIZE/2);
        outputStream.close();

        assertDestFileContains(Arrays.copyOfRange(bytes,0,BUFFER_SIZE*2+BUFFER_SIZE/2) );
    }

    @Test
    public void usingThisClassIsFasterThanFileOutputStream() throws IOException {
        byte[]       bytes     = PrimitiveGenerators.strings(1024*100,1024*100).next().getBytes();
        File         destFile2 = File.createTempFile("PreAllocatingFileOutputStreamTest_latencytest_", ".dat");
        OutputStream out2      = new FileOutputStream(destFile2);

        long durationNanos2 = writeBytesToFileStream( out2, bytes );
        long durationNanos1 = writeBytesToFileStream( outputStream, bytes );


        System.out.println("durationNanos1 = " + durationNanos1);
        System.out.println("durationNanos2 = " + durationNanos2);

        assertTrue( durationNanos1 < durationNanos2 );
    }


    private long writeBytesToFileStream( OutputStream out, byte[] bytes ) throws IOException {
        long startNanos = System.nanoTime();

        out.write(bytes);
        out.close();

        long endNanos = System.nanoTime();

        return endNanos - startNanos;
    }


    private void assertDestFileContains( byte[] expectedBytes ) throws IOException {
        assertEquals(expectedBytes.length, destFile.length());

        RandomAccessFile f = new RandomAccessFile(destFile,"r");

        try {
            byte[] inputBytes = new byte[expectedBytes.length];

            int numBytesRead = f.read( inputBytes );

            assertEquals( expectedBytes.length, numBytesRead );
            assertArrayEquals( expectedBytes, inputBytes );
        } finally {
            f.close();
        }
    }

}
