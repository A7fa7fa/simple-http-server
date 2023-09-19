package org.a7fa7fa.httpserver.http;

import org.a7fa7fa.httpserver.http.tokens.HeaderName;
import org.a7fa7fa.httpserver.http.tokens.HttpStatusCode;
import org.a7fa7fa.httpserver.http.tokens.HttpVersion;
import org.a7fa7fa.httpserver.staticcontent.Reader;

import java.io.IOException;
import java.nio.file.Path;

public class HttpResponseGET extends HttpResponse {


    private final String webroot;
    public HttpResponseGET(HttpVersion httpVersion, HttpStatusCode statusCode, String webroot) {
        super(httpVersion, statusCode);
        this.webroot = webroot;
    }


    @Override
    public void handleRequest(HttpRequest httpRequest, int  gzipMinFileSizeKb) throws IOException, HttpParsingException {
        Path filePath = Reader.getFilePath(this.webroot, httpRequest.getRequestTarget());
        String contentType = Reader.probeContentType(filePath);
        if (contentType != null) {
            if (this.clientNotUnderstandsType(httpRequest, contentType)) {
                throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_415_UNSUPPORTED_MEDIA_TYPE);
            }
            this.addHeader(new HttpHeader(HeaderName.CONTENT_TYPE, contentType));
        }
        byte[] body = Reader.readFile(filePath);
        // TODO You should not allow your web server to compress image files or PDF files
        // these files are already compressed and by compressing them again you’re not only wasting CPU resources but you can actually make the resulting file larger by compressing them again
        String encodingToken = "gzip";
        HttpHeader encodingHeader = httpRequest.getHeader(HeaderName.ACCEPT_ENCODING);
        if (encodingHeader != null && encodingHeader.getValue().contains(encodingToken) && body.length / 1024 >  gzipMinFileSizeKb) {
            int sizeBeforeCompressing = body.length;
            body = Reader.compress(body);
            this.addHeader(new HttpHeader(HeaderName.CONTENT_ENCODING, encodingToken));
            LOGGER.info("Request encoded : {} - size kb before/after {}/{}", encodingToken, sizeBeforeCompressing, body.length);
        }
        this.addDefaultHeader();
        this.addBody(body);
        this.addHeader(new HttpHeader(HeaderName.CONTENT_LENGTH, String.valueOf(body.length)));
    }
}
