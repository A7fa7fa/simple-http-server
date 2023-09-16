package org.a7fa7fa.httpserver.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;


public class HttpResponse extends HttpMessage {

    final static Logger LOGGER = LoggerFactory.getLogger(HttpResponse.class);
    private final HttpStatusCode statusCode;
    private final HttpVersion httpVersion;

    private String headers = "";
    private byte[] body;

    private final String CRLF = "\r\n";

    public HttpResponse(HttpVersion httpVersion, HttpStatusCode statusCode){
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
        this.body = new byte[0];
    }

    public String getStatusLine() { // HTTP-version SP status-code SP reason-phrase //CRLF
        String SP = " ";
        StringBuilder sb = new StringBuilder();
        sb.append(this.httpVersion.LITERAL);
        sb.append(SP);
        sb.append(this.statusCode.STATUS_CODE);
        sb.append(SP);
        sb.append(this.statusCode.MESSAGE);
//        sb.append(CRLF);
        return sb.toString();
    }

    public void addBody(byte[] body){
        this.body = body;
    }

    public void addHeader(HttpHeader httpHeader){
        this.headers += httpHeader.toStandardFormat() + CRLF;
    }

    private void printMessage(byte[] message) {
        for (byte _byte: message ){
            System.out.print((char)_byte);
        }
    }

    public byte[] getBytes(){
        byte[] message = concatResponse(this.getStatusLine().getBytes(), this.CRLF.getBytes(), this.headers.getBytes(), this.CRLF.getBytes(), this.body, CRLF.getBytes(), CRLF.getBytes());
        LOGGER.info("Respond with: {}", this.getStatusLine());
        // this.printMessage(message);
        return message;
    }

    public void handleRequest(HttpRequest httpRequest, int  gzipMinFileSizeKb) throws IOException, HttpParsingException {
        throw new RuntimeException("Not implemented");
    }

    public boolean clientNotUnderstandsType(HttpRequest httpRequest, String contentType) {
        HttpHeader header = httpRequest.getHeader(HeaderName.ACCEPT);
        if (header == null) {
            return false;
        }
        return !(header.getValue().contains(contentType) || header.getValue().contains("*/*"));
    }

    public void pipe(OutputStream outputStream) throws IOException {
        outputStream.write(this.getBytes());
    }

    public static byte[] concatResponse(byte[]...arrays)
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
}
