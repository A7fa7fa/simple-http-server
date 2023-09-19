package org.a7fa7fa.httpserver.http;

import org.a7fa7fa.httpserver.http.tokens.HeaderName;
import org.a7fa7fa.httpserver.http.tokens.HttpStatusCode;
import org.a7fa7fa.httpserver.http.tokens.HttpVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;


public class HttpResponse extends HttpMessage {

    final static Logger LOGGER = LoggerFactory.getLogger(HttpResponse.class);
    private HttpStatusCode statusCode;
    private final HttpVersion httpVersion;

    private String headers = "";
    private byte[] body;

    private final String CRLF = "\r\n";

    public HttpResponse(HttpVersion httpVersion, HttpStatusCode statusCode){
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
        this.body = new byte[0];
    }

    public void setStatusCode(HttpStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusLine() { // HTTP-version SP status-code SP reason-phrase //CRLF
        String SP = " ";
        StringBuilder sb = new StringBuilder();
        sb.append(this.httpVersion.LITERAL);
        sb.append(SP);
        sb.append(this.statusCode.STATUS_CODE);
        sb.append(SP);
        sb.append(this.statusCode.MESSAGE);
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

    public void addDefaultHeader() {
        this.addHeader(new HttpHeader(HeaderName.SERVER, "simple-http-server"));
        this.addHeader(new HttpHeader(HeaderName.DATE, this.getServerTime()));

    }

    public byte[] getBytes(){
        byte[] message;
        if (this.body.length == 0) {
            message = concatResponse(this.getStatusLine().getBytes(), this.CRLF.getBytes(), this.headers.getBytes(), this.CRLF.getBytes());
        } else {
            message = concatResponse(this.getStatusLine().getBytes(), this.CRLF.getBytes(), this.headers.getBytes(), this.CRLF.getBytes(), this.body);
        }

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

    String getServerTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(calendar.getTime());
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
