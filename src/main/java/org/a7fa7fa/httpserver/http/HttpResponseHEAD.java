package org.a7fa7fa.httpserver.http;

import org.a7fa7fa.httpserver.root.Reader;

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
        this.addHeader(new HttpHeader(HeaderName.SERVER, "simple-http-server"));
        String contentType = Reader.probeContentType(filePath);
        if (contentType != null) {
            if (this.clientNotUnderstandsType(httpRequest, contentType)) {
                throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_415_UNSUPPORTED_MEDIA_TYPE);
            }
            this.addHeader(new HttpHeader(HeaderName.CONTENT_TYPE, Reader.probeContentType(filePath)));
        }
        this.addHeader(new HttpHeader(HeaderName.DATE, this.getServerTime()));
        this.addBody(new byte[0]);
        this.addHeader(new HttpHeader(HeaderName.CONTENT_LENGTH, String.valueOf(Reader.getFileSize(filePath))));
    }
}
