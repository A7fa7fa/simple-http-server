package org.a7fa7fa.httpserver.http;

import org.a7fa7fa.httpserver.http.tokens.HeaderName;
import org.a7fa7fa.httpserver.http.tokens.HttpStatusCode;
import org.a7fa7fa.httpserver.http.tokens.HttpVersion;
import org.a7fa7fa.httpserver.parser.ByteProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;


public class HttpResponse extends HttpMessage {

    final static Logger LOGGER = LoggerFactory.getLogger(HttpResponse.class);
    private HttpStatusCode statusCode;
    private final HttpVersion httpVersion;

    private String headers = "";
    private byte[] body = new byte[0];

    private final String CRLF = "\r\n";

    public HttpResponse(HttpVersion httpVersion){
    this.httpVersion = httpVersion;
}

    public String toString() {
        return this.statusCode.toString();
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

    public String getHeaders(){
        return this.headers;
    }

    public void setDefaultHeader() {
        this.addHeader(new HttpHeader(HeaderName.SERVER, "simple-http-server"));
        this.addHeader(new HttpHeader(HeaderName.DATE, this.getServerTime()));
        this.addHeader(new HttpHeader(HeaderName.HOST, "localhost"));
    }

    public byte[] buildStatusWithHeaders() {
        return ByteProcessor.combine(this.getStatusLine().getBytes(), this.CRLF.getBytes(), this.headers.getBytes(), this.CRLF.getBytes());
    }

    public byte[] buildCompleteMessage(){
        byte[] message = ByteProcessor.combine(this.getStatusLine().getBytes(), this.CRLF.getBytes(), this.headers.getBytes(), this.CRLF.getBytes(), this.body);

        LOGGER.info("Respond with: {}", this.getStatusLine());
        // this.printMessage(message);
        return message;
    }

    public void handleRequest(HttpRequest httpRequest, int  gzipMinFileSizeKb) throws IOException, HttpParsingException {
        throw new RuntimeException("Not implemented");
    }

    public void pipe(OutputStream outputStream, byte[] data) throws IOException {
        if (this.statusCode == null) {
            LOGGER.warn("Status code not set");
            throw new RuntimeException("Status code not set");
        }
        outputStream.write(data);
    }

    String getServerTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(calendar.getTime());
    }

    public byte[] createChunk(byte[] data) {
        String chunkSize = Integer.toHexString(data.length);
        return ByteProcessor.combine(chunkSize.getBytes(StandardCharsets.US_ASCII), this.CRLF.getBytes(StandardCharsets.US_ASCII), data, this.CRLF.getBytes(StandardCharsets.US_ASCII));
    }

    public byte[] createEndStreamChunk() {
        return this.createChunk(new byte[0]);
    }


}
