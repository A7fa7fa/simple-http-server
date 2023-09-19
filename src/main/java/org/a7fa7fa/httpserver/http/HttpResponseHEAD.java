package org.a7fa7fa.httpserver.http;

import org.a7fa7fa.httpserver.http.tokens.HeaderName;
import org.a7fa7fa.httpserver.http.tokens.HttpStatusCode;
import org.a7fa7fa.httpserver.http.tokens.HttpVersion;
import org.a7fa7fa.httpserver.staticcontent.Reader;

import java.io.IOException;
import java.nio.file.Path;

public class HttpResponseHEAD extends HttpResponse {
    private final String webroot;

    public HttpResponseHEAD(HttpVersion httpVersion, HttpStatusCode statusCode, String webroot) {
        super(httpVersion, statusCode);
        this.webroot = webroot;
    }

    public void handleRequest(HttpRequest httpRequest, int  gzipMinFileSizeKb) throws IOException, HttpParsingException {
        Path filePath = Reader.getFilePath(this.webroot, httpRequest.getRequestTarget());
        String contentType = Reader.probeContentType(filePath);
        if (contentType != null) {
            if (this.clientNotUnderstandsType(httpRequest, contentType)) {
                throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_415_UNSUPPORTED_MEDIA_TYPE);
            }
            this.addHeader(new HttpHeader(HeaderName.CONTENT_TYPE, Reader.probeContentType(filePath)));
        }
        this.addDefaultHeader();
        this.addBody(new byte[0]);
        this.addHeader(new HttpHeader(HeaderName.CONTENT_LENGTH, String.valueOf(Reader.getFileSize(filePath))));
    }
}
