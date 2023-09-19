package org.a7fa7fa.httpserver.core;

import org.a7fa7fa.httpserver.http.*;
import org.a7fa7fa.httpserver.http.tokens.HeaderName;
import org.a7fa7fa.httpserver.http.tokens.HttpStatusCode;
import org.a7fa7fa.httpserver.http.tokens.HttpVersion;
import org.a7fa7fa.httpserver.router.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HttpConnectionWorkerThread extends Thread {
    private final static Logger LOGGER = LoggerFactory.getLogger(HttpConnectionWorkerThread.class);

    private final Socket socket;
    private final String webroot;
    private final int gzipMinFileSizeKb;
    private final String apiPath;

    public HttpConnectionWorkerThread(Socket socket, String webroot, int gzipMinFileSizeKb, String apiPath) {
        this.socket = socket;
        this.webroot = webroot;
        this.apiPath = apiPath;
        this.gzipMinFileSizeKb = gzipMinFileSizeKb;
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            HttpParser httpParser = new HttpParser();
            HttpRequest request = httpParser.parseHttpRequest(inputStream);
            if (request.getMethod() == null){
                LOGGER.debug("Closing empty request");
                return;
            }

            if (request.getRequestTarget().startsWith(this.apiPath)) {
                LOGGER.debug("Api call made");
                Router router = Router.getInstance();
                router.invoke(request);
                throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_501_NOT_IMPLEMENTED);
            } else {
                HttpResponse httpResponse = HttpResponseFactory.generateResponseFor(request, this.webroot);
                httpResponse.handleRequest(request, gzipMinFileSizeKb);
                httpResponse.pipe(outputStream);
            }

        } catch (HttpParsingException e) {
            LOGGER.error("Problem with communication", e);
            if (outputStream != null) {
                HttpResponse response = new HttpResponse(HttpVersion.HTTP_1_1, e.getErrorCode());
                response.addDefaultHeader();
                response.addHeader(new HttpHeader(HeaderName.CONTENT_LENGTH, "0"));
                LOGGER.debug("Error response set : {}", response.getStatusLine());
                try {
                    outputStream.write(response.getBytes());
                } catch (IOException ex) {}
            }
        } catch (IOException e) {
            LOGGER.error("Problem with reading from file", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {}
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {}
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {}
            }
            LOGGER.info("Processing finished. Connection closed.");
        }
    }
}
