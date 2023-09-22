package org.a7fa7fa.httpserver.http;

import org.a7fa7fa.httpserver.config.Configuration;
import org.a7fa7fa.httpserver.http.tokens.HeaderName;
import org.a7fa7fa.httpserver.http.tokens.HttpStatusCode;
import org.a7fa7fa.httpserver.staticcontent.Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

public class Context {
    private final static Logger LOGGER = LoggerFactory.getLogger(Context.class);
    private final HttpRequest httpRequest;
    private final HttpResponse httpResponse;
    private final Configuration configuration;

    public Context(HttpRequest httpRequest, HttpResponse httpResponse, Configuration configuration) {
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
        this.configuration = configuration;
    }

    public String toString() {
        return this.httpRequest.toString() + " - " + this.httpResponse.toString();
    }

    public HttpRequest getHttpRequest() {
        return httpRequest;
    }

    public HttpResponse getHttpResponse() {
        return httpResponse;
    }

    public void handleRequest() throws IOException, HttpParsingException {
        httpResponse.handleRequest(httpRequest, 5);
    }

    public String getRequestTarget() {
        return httpRequest.getRequestTarget();
    }

    public byte[] readStaticFile(String targetFile) throws IOException, HttpParsingException {

        Path filePath = Reader.getFilePath(this.configuration.getWebroot(), targetFile);
        String contentType = Reader.probeContentType(filePath);

        if (contentType != null) {
            if (this.httpRequest.clientNotUnderstandsType(contentType)) {
                throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_415_UNSUPPORTED_MEDIA_TYPE);
            }
            this.httpResponse.addHeader(new HttpHeader(HeaderName.CONTENT_TYPE, contentType));
        }
        return Reader.readFile(filePath);
    }

    public void addBodyToResponse(byte[] body) throws IOException {
        // TODO You should not allow your web server to compress image files or PDF files
        // these files are already compressed and by compressing them again you’re not only wasting CPU resources but you can actually make the resulting file larger by compressing them again
        String encodingToken = "gzip";
        HttpHeader encodingHeader = httpRequest.getHeader(HeaderName.ACCEPT_ENCODING);
        if (encodingHeader != null && encodingHeader.getValue().contains(encodingToken) && body.length / 1024 >  this.configuration.getGzipMinFileSizeKb()) {
            int sizeBeforeCompressing = body.length;
            body = Reader.compress(body);
            this.httpResponse.addHeader(new HttpHeader(HeaderName.CONTENT_ENCODING, encodingToken));
            LOGGER.debug("Request encoded : {} - size kb before/after {}/{}", encodingToken, sizeBeforeCompressing, body.length);
        }
        this.httpResponse.addBody(body);
        this.httpResponse.addHeader(new HttpHeader(HeaderName.CONTENT_LENGTH, String.valueOf(body.length)));
        this.setResponseStatus(HttpStatusCode.SUCCESSFUL_RESPONSE_200_OK);
    }

    public void setResponseStatus(HttpStatusCode code) {
        this.httpResponse.setStatusCode(code);
    }

    public void addDefaultResponseHeader(){
        this.httpResponse.addDefaultHeader();
    }

}

