package org.a7fa7fa.httpserver.parser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

public class ByteProcessor {

    public static byte[] combine(byte[]...arrays)
    {
        // Determine the length of the result array
        int totalLength = 0;
        for (int i = 0; i < arrays.length; i++)
        {
            totalLength += arrays[i].length;
        }

        // create the result array
        byte[] result = new byte[totalLength];

        // copy the source arrays into the result array
        int currentIndex = 0;
        for (int i = 0; i < arrays.length; i++)
        {
            System.arraycopy(arrays[i], 0, result, currentIndex, arrays[i].length);
            currentIndex += arrays[i].length;
        }

        return result;
    }

    public static String byteToString(byte[] message) {
        StringBuilder sb = new StringBuilder();
        for (byte _byte: message) {
            sb.append((char) _byte);
        }
        return sb.toString();
    }

    public static byte[] compress(byte[] file) throws IOException {

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        GZIPOutputStream zipStream = new GZIPOutputStream(byteStream);
        zipStream.write(file);
        zipStream.close();
        byte[] byteBody = byteStream.toByteArray();
        return byteBody;
    }

}
