package org.a7fa7fa.httpserver.core;

import org.a7fa7fa.httpserver.config.Configuration;
import org.a7fa7fa.httpserver.http.*;
import org.a7fa7fa.httpserver.http.tokens.HeaderName;
import org.a7fa7fa.httpserver.http.tokens.HttpStatusCode;
import org.a7fa7fa.httpserver.http.tokens.HttpVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HttpConnectionWorkerThread extends Thread {
    private final static Logger LOGGER = LoggerFactory.getLogger(HttpConnectionWorkerThread.class);

    private final Socket socket;
    private final Configuration configuration;

    public HttpConnectionWorkerThread(Socket socket, Configuration configuration) {
        this.socket = socket;
        this.configuration = configuration;
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

            HttpResponse response = new HttpResponse(request.getBestCompatibleHttpVersion());

            Router router = Router.getInstance(configuration);
            router.invoke(request, response);
            response.pipe(outputStream);


        } catch (RuntimeException e) {
            LOGGER.error("Runtime caught.");
            Throwable originalException = e.getCause();
            HttpStatusCode code = HttpStatusCode.CLIENT_ERROR_500_INTERNAL_SEVER_ERROR;
            if (originalException instanceof HttpParsingException) {
                code = ((HttpParsingException) originalException).getErrorCode();
            }

            if (outputStream != null) {
                HttpResponse response = new HttpResponse(HttpVersion.HTTP_1_1);
                response.setStatusCode(code);
                response.addDefaultHeader();
                response.addHeader(new HttpHeader(HeaderName.CONTENT_LENGTH, "0"));
                LOGGER.debug("Error response set : {}", response.getStatusLine());
                try {
                    outputStream.write(response.getBytes());
                } catch (IOException ex) {}
            }

        } catch (HttpParsingException e) {
            LOGGER.error("Problem with communication", e);
            if (outputStream != null) {
                HttpResponse response = new HttpResponse(HttpVersion.HTTP_1_1);
                response.setStatusCode(e.getErrorCode());
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
