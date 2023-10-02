package org.a7fa7fa.httpserver.core;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MockSocket extends Socket {

    private byte[] input;

    private OutputStream output;

    private List<Byte> bytesList = new ArrayList<>();

    public MockSocket() {
        this.output = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                bytesList.add((byte) b);
            }
        };
    }

    public void setInput(String input) {
        this.input = input.getBytes(StandardCharsets.US_ASCII);
    }

    public byte[] getBytesList() {
        byte[] temp = new byte[this.bytesList.size()];
        for (int i = 0; i < bytesList.size(); i++) {
            temp[i] = bytesList.get(i);
        }
        return temp;
    }

    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.input);
    }

    public OutputStream getOutputStream() {
        return this.output;
    }
}
