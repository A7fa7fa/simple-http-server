package org.a7fa7fa.httpserver.http;

import org.a7fa7fa.httpserver.http.tokens.HeaderName;
import org.a7fa7fa.httpserver.http.tokens.HttpStatusCode;
import org.a7fa7fa.httpserver.http.tokens.HttpVersion;
import org.a7fa7fa.httpserver.parser.ByteProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import org.a7fa7fa.httpserver.config.Configuration;
import org.a7fa7fa.httpserver.config.ConfigurationManager;


public class HttpResponse extends HttpMessage {

    final static Logger LOGGER = LoggerFactory.getLogger(HttpResponse.class);
    private HttpStatusCode statusCode;
    private final HttpVersion httpVersion;
    private final HashMap<String, HttpHeader> httpHeaders = new HashMap<String, HttpHeader>();
    private byte[] body = new byte[0];
    private final String CRLF = "\r\n";
    private boolean alreadySend = false;

    public HttpResponse(HttpVersion httpVersion){
        this.httpVersion = httpVersion;
    }

    String getContentType() {
        HttpHeader content = this.httpHeaders.get(HeaderName.CONTENT_TYPE.getName());
        if (content != null) {
            return content.getValue();
        }
        return null;
    }

    void setContentType(String contentType) {
        if (contentType == null) {
            return;
        }
        this.addHeader(new HttpHeader(HeaderName.CONTENT_TYPE, contentType));
    }

    public boolean isAlreadySend() {
        return alreadySend;
    }

    void setAlreadySend(boolean alreadySend) {
        this.alreadySend = alreadySend;
    }

    public String toString() {
        return this.statusCode.toString();
    }


    public void setStatusCode(HttpStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public String getStatusLine() { // HTTP-version SP status-code SP reason-phrase //CRLF
        if (this.statusCode == null) {
            this.statusCode = HttpStatusCode.CLIENT_ERROR_500_INTERNAL_SEVER_ERROR;
        }
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
        this.httpHeaders.put(httpHeader.getHeaderField().getName(), httpHeader);
    }

    public String getHttpHeaders(){
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, HttpHeader> header : this.httpHeaders.entrySet()) {
            sb.append(header.getValue().toStandardFormat());
            sb.append(CRLF);
        }
        return sb.toString();
    }

    public HttpHeader getHeader(HeaderName name){
        return this.httpHeaders.get(name.getName());
    }

    public void setDefaultHeader(Configuration config) {
        if (this.httpHeaders.get(HeaderName.SERVER.getName()) == null) this.addHeader(new HttpHeader(HeaderName.SERVER, "simple-http-server"));
        if (this.httpHeaders.get(HeaderName.DATE.getName()) == null) this.addHeader(new HttpHeader(HeaderName.DATE, this.getServerTime()));
        if (this.httpHeaders.get(HeaderName.HOST.getName()) == null) this.addHeader(new HttpHeader(HeaderName.HOST, config.getHost()));
        if (this.httpHeaders.get(HeaderName.CONNECTION.getName()) == null) this.addHeader(new HttpHeader(HeaderName.CONNECTION, "close"));
    }

    public byte[] buildStatusWithHeaders() {
        return ByteProcessor.combine(this.getStatusLine().getBytes(), this.CRLF.getBytes(), this.getHttpHeaders().getBytes(), this.CRLF.getBytes());
    }

    public byte[] buildCompleteMessage() {
        byte[] message = ByteProcessor.combine(this.getStatusLine().getBytes(), this.CRLF.getBytes(), this.getHttpHeaders().getBytes(), this.CRLF.getBytes(), this.body);

        LOGGER.info("Respond with: {}", this.getStatusLine());
        // this.printMessage(message);
        return message;
    }

    private String getServerTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(calendar.getTime());
    }

    public byte[] createChunk(byte[] data) {
        String chunkSize = Integer.toHexString(data.length);
        return ByteProcessor.combine(chunkSize.getBytes(StandardCharsets.US_ASCII), this.CRLF.getBytes(StandardCharsets.US_ASCII), data, this.CRLF.getBytes(StandardCharsets.US_ASCII));
    }

}
