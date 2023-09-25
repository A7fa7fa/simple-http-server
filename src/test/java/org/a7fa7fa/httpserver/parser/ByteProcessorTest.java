package org.a7fa7fa.httpserver.parser;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;


class ByteProcessorTest {

    @Test
    void combineTest() {
        String firstLine = "this is a line";
        String secondLine = "this is another line";

        byte[] concatenated = ByteProcessor.combine(firstLine.getBytes(StandardCharsets.US_ASCII), secondLine.getBytes(StandardCharsets.UTF_8));

        assertEquals(ByteProcessor.byteToString(concatenated), firstLine + secondLine);
    }

}