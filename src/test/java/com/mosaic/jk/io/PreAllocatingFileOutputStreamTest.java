package com.mosaic.jk.io;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.java.quickcheck.Generator;
import net.java.quickcheck.generator.PrimitiveGenerators;

import java.io.*;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 *
 */
public class PreAllocatingFileOutputStreamTest {
    private static int BUFFER_SIZE = 20;

    private static Generator<String> LB_GENERATOR = PrimitiveGenerators.strings(BUFFER_SIZE-1);
    private static Generator<String> EB_GENERATOR = PrimitiveGenerators.strings(BUFFER_SIZE);
    private static Generator<String> GB_GENERATOR = PrimitiveGenerators.strings(BUFFER_SIZE*3);

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


    // appendTwoBytesInARow
    // appendTwoArraysInARow



    // writePartOfArrayLessThanBufferSizeUsing_expectFileToContainSpecifiedBytes
    // writeArrayCrossingOverBufferSizeUsing_expectFileToContainSpecifiedBytes
    // writePartOfArrayCrossingOverBufferSizeUsing_expectFileToContainSpecifiedBytes
    // writeArrayExactlyOnBufferSizeUsing_expectFileToContainSpecifiedBytes
    // writePartOfArrayExactlyOnBufferSizeUsing_expectFileToContainSpecifiedBytes

    // appendMultipleArraysCrossingSeveralBufferSizeBoundaries


    // usingThisClassIsFasterThatFileOutputStream


    private void assertDestFileContains( byte[] expectedBytes ) throws IOException {
        assertEquals( expectedBytes.length, destFile.length() );

        RandomAccessFile f = new RandomAccessFile(destFile,"r");

        try {
            byte[] inputBytes = new byte[expectedBytes.length];

            f.read( inputBytes );

            assertArrayEquals( expectedBytes, inputBytes );
        } finally {
            f.close();
        }
    }

}
