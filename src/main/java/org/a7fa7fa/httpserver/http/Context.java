package org.a7fa7fa.httpserver.http;

import org.a7fa7fa.httpserver.config.Configuration;
import org.a7fa7fa.httpserver.http.tokens.HeaderName;
import org.a7fa7fa.httpserver.http.tokens.HttpStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

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

    public HttpRequest getHttpRequest() {
        return httpRequest;
    }

    public byte[] readContentFromFile(String fileLocation) throws HttpParsingException, IOException {
        return this.responseProcessor.readDataFromAbsoluteFile(this.httpRequest, fileLocation);
    }
    public byte[] readTargetFromFile() throws HttpParsingException, IOException {
        return this.responseProcessor.readDataFromFile(this.httpRequest, this.configuration.getWebroot());
    }
    public void setResponse(byte[] data) throws IOException, HttpParsingException {
        this.responseProcessor.prepareResponse(data, this.httpRequest, this.configuration.getGzipMinFileSizeKb());
    }
    public void setResponseStatus(HttpStatusCode code) {
        this.responseProcessor.getResponse().setStatusCode(code);
    }

    public void setDefaultResponseHeader(){
        this.responseProcessor.getResponse().setDefaultHeader();
    }

    public void send() throws IOException {
        this.responseProcessor.sendFullMessage();
    }

    public void sendStatusAndHeader() throws IOException {
        this.responseProcessor.sendWithoutBody();
    }

    public void streamData(byte[] data) throws IOException {

        ByteArrayInputStream inBuffer = new ByteArrayInputStream(data);
        this.responseProcessor.streamFromStream(inBuffer, 20);
    }

    public void streamData(FileInputStream inputStream) throws IOException {
        this.responseProcessor.streamFromStream(inputStream, 1024*10);
    }
}

