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
import java.net.SocketException;

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

        long start = System.currentTimeMillis();

        try {

            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            while(true) {
                if (inputStream.available() == 0) {
                    long curr = System.currentTimeMillis();
                    if (((curr - start) / 1000) > 20 ) {
                        LOGGER.info("Connection closing");
                        break;
                    }
                    continue;
                }
                start = System.currentTimeMillis();

                HttpParser httpParser = new HttpParser();
                HttpRequest request = httpParser.parseHttpRequest(inputStream, configuration.getMaxBodySize());
                if (request.getMethod() == null){
                    LOGGER.debug("Closing empty request");
                    return;
                }

                HttpResponse response = new HttpResponse(request.getBestCompatibleHttpVersion());

                Router router = Router.getInstance(configuration);
                ResponseProcessor responseProcessor = new ResponseProcessor(response, outputStream);
                Context context = new Context(request, this.configuration, responseProcessor);
                router.invoke(context);
                if (!response.isAlreadySend()) {
                    byte[] responseMessage = response.buildCompleteMessage();
                    responseProcessor.pipe(responseMessage);
                }

                if (!request.isPersistentConnection()) {
                    break;
                }
                LOGGER.debug("Persistent connection");
            }


        } catch (RuntimeException e) {
            LOGGER.debug("Runtime caught " + e);
            LOGGER.debug("Runtime caught " + e.getCause());
            Throwable originalException = e.getCause();
            HttpStatusCode code = HttpStatusCode.CLIENT_ERROR_500_INTERNAL_SEVER_ERROR;
            if (originalException instanceof HttpParsingException) {
                code = ((HttpParsingException) originalException).getErrorCode();
            }
            if (originalException instanceof SocketException || originalException instanceof ClientDisconnectException) {
                LOGGER.debug("Client disconnected so just return.");
                return;
            }

            if (outputStream != null) {
                HttpResponse response = new HttpResponse(HttpVersion.HTTP_1_1);
                response.setStatusCode(code);
                response.setDefaultHeader();
                LOGGER.error("Error response set : {}", response.getStatusLine());
                try {
                    outputStream.write(response.buildCompleteMessage());
                } catch (IOException ex) {}
            }

        } catch (HttpParsingException e) {
            LOGGER.error("Problem with communication", e);
            if (outputStream != null) {
                HttpResponse response = new HttpResponse(HttpVersion.HTTP_1_1);
                response.setStatusCode(e.getErrorCode());
                response.setDefaultHeader();
                LOGGER.debug("Error response set : {}", response.getStatusLine());
                try {
                    outputStream.write(response.buildCompleteMessage());
                } catch (IOException ex) {}
            }
        } catch (IOException | ClientDisconnectException e) {
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
