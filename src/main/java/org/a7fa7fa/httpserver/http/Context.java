package org.a7fa7fa.httpserver.http;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import org.a7fa7fa.httpserver.config.Configuration;
import org.a7fa7fa.httpserver.http.exceptions.ClientDisconnectException;
import org.a7fa7fa.httpserver.http.exceptions.HttpParsingException;
import org.a7fa7fa.httpserver.http.tokens.HeaderName;
import org.a7fa7fa.httpserver.http.tokens.HttpStatusCode;
import org.a7fa7fa.httpserver.parser.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

public class Context {

    private final static Logger LOGGER = LoggerFactory.getLogger(Context.class);
    private final HttpRequest httpRequest;
    private final Configuration configuration;
    private final ResponseProcessor responseProcessor;

    public Context(HttpRequest httpRequest, Configuration configuration, ResponseProcessor responseProcessor) {
        this.httpRequest = httpRequest;
        this.configuration = configuration;
        this.responseProcessor = responseProcessor;
    }

    public void addHeader(HttpHeader header) {
        this.responseProcessor.getResponse().addHeader(header);
    }

    public String toString() {
        return this.httpRequest.toString() + " - " + this.responseProcessor.getResponse().toString();
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public String getCookieValue(String cookieName) {
        HttpHeader cookieHeader = httpRequest.getHeader(HeaderName.COOKIE);
        if (cookieHeader == null) return null;

        return cookieHeader.getValue();

    }

    public HttpRequest getHttpRequest() {
        return httpRequest;
    }

    public HttpResponse getHttpResponse() {
        return this.responseProcessor.getResponse();
    }

    public byte[] readContentFromFile(String fileLocation) throws HttpParsingException, IOException {
        return this.responseProcessor.readDataFromAbsoluteFile(this.httpRequest, fileLocation);
    }

    public byte[] readTargetFromFile() throws HttpParsingException, IOException {
        return this.responseProcessor.readDataFromFile(this.httpRequest, this.configuration.getWebroot());
    }

    public void setResponse(Object obj) throws IOException {
        JsonNode node = Json.toJson(obj);
        String data = Json.stringify(node);
        this.responseProcessor.prepareResponse(data.getBytes(), this.httpRequest, this.configuration.getGzipMinFileSizeKb());
        this.addHeader(new HttpHeader(HeaderName.CONTENT_TYPE, "application/json"));
        this.setDefaultResponseHeader();
        
    }

    public void setResponse(byte[] data) throws IOException {
        this.responseProcessor.prepareResponse(data, this.httpRequest, this.configuration.getGzipMinFileSizeKb());
        this.setDefaultResponseHeader();
    }

    public void setResponseStatus(HttpStatusCode code) {
        this.responseProcessor.getResponse().setStatusCode(code);
        this.setDefaultResponseHeader();
    }

    private void setDefaultResponseHeader(){
        this.responseProcessor.getResponse().setDefaultHeader(this.configuration);
        if (this.httpRequest.isPersistentConnection()) {
            this.addHeader(new HttpHeader(HeaderName.CONNECTION, "keep-alive"));
        }
    }

    public void send() throws ClientDisconnectException {
        this.responseProcessor.sendFullMessage();
    }

    public void sendStatusAndHeader() throws ClientDisconnectException {
        this.responseProcessor.sendWithoutBody();
    }

    public void streamData(byte[] data) throws ClientDisconnectException, IOException {
        this.streamData(data, 20);
    }

    public void sendAsChunk(byte[] data) throws ClientDisconnectException {
        this.responseProcessor.sendChunk(data);
    }

    public void endStream() throws ClientDisconnectException {
        this.responseProcessor.endStream();
    }

    public void streamData(byte[] data, int chunkSize) throws ClientDisconnectException, IOException {

        ByteArrayInputStream inBuffer = new ByteArrayInputStream(data);
        this.responseProcessor.streamFromStream(inBuffer, chunkSize);
    }

    public void streamData(FileInputStream inputStream) throws ClientDisconnectException, IOException {
        this.streamData(inputStream, 1024*10);
    }

    public void streamData(FileInputStream inputStream, int chunkSize) throws ClientDisconnectException, IOException {
        this.responseProcessor.streamFromStream(inputStream, chunkSize);
    }

}

