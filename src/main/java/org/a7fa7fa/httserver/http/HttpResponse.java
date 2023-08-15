package org.a7fa7fa.httserver.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HttpResponse extends HttpMessage {
    private final static Logger LOGGER = LoggerFactory.getLogger(HttpResponse.class);
    private HttpVersion httpVersion;

    private String statusLine; // HTTP-version SP status-code SP reason-phrase CRLF
    private String headerContentLength = "";

    private String headers = "";
    private byte[] body;

    private final String SP = " ";
    private final String NEW_LINE = "\r\n";

    public HttpResponse(){
    }

    public void setStatusLine(HttpVersion httpVersion, HttpStatusCode statusCode) {
        this.statusLine = httpVersion.LITERAL + SP + statusCode.STATUS_CODE + SP + statusCode.MESSAGE + NEW_LINE;
    }

    public void addBody(byte[] body){
        this.body = body;
        this.setHeaderContentLength(body.length);
    }

    public void setHeaderContentLength(long size) {
        this.headerContentLength = "Content-Length:" + SP + size + NEW_LINE;
    }

    public void addHeader(HttpHeader httpHeader){
        this.headers = httpHeader.getOriginalFieldName() + SP + httpHeader.getOriginalFieldValue() + NEW_LINE;
    }

    private void printMessage(byte[] message) {
        for (byte _byte: message ){
            System.out.print((char)_byte);
        }
    }

    public byte[] getBytes(){
        byte[] data = this.body;
        if (this.body == null ) {
            data = new byte[0];
        }
        HttpHeader serverName = new HttpHeader();
        serverName.setFieldName("Server:");
        serverName.setFieldValue("My micro Java Server");
        this.addHeader(serverName);
        byte[] message = concatResponse(this.statusLine.getBytes(), this.headerContentLength.getBytes(), this.headers.getBytes(), NEW_LINE.getBytes(), data, NEW_LINE.getBytes(), NEW_LINE.getBytes());
        LOGGER.info("Respond with: {}", this.statusLine);
        // this.printMessage(message);
        return message;
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
