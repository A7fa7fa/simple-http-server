package org.a7fa7fa.httserver.core;

import org.a7fa7fa.httserver.root.Reader;
import org.a7fa7fa.httserver.http.*;

import java.io.IOException;
import java.nio.file.Path;

public class RequestHandler {


    private final String webroot;

    public RequestHandler(String webroot) {
        this.webroot = webroot;
    }

    public HttpResponse generateResponse(HttpRequest httpRequest) throws IOException, HttpParsingException {
        HttpResponse httpResponse = new HttpResponse();

        Path filePath = Reader.getFilePath(this.webroot, httpRequest.getRequestTarget());
        httpResponse.setStatusLine(HttpVersion.HTTP_1_1, HttpStatusCode.SUCCESSFUL_RESPONSE_200_OK);

        if (httpRequest.getMethod() == HttpMethod.HEAD) {
            httpResponse.setHeaderContentLength(Reader.getFileSize(filePath));
        } else {
            byte[] body = Reader.readFile(filePath);
            httpResponse.addBody(body);
        }
        return httpResponse;
    }
}
