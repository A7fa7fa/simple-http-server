package org.a7fa7fa.httpserver.controller;

import org.a7fa7fa.httpserver.http.Context;
import org.a7fa7fa.httpserver.http.HttpHeader;
import org.a7fa7fa.httpserver.http.HttpParsingException;
import org.a7fa7fa.httpserver.http.tokens.HeaderName;
import org.a7fa7fa.httpserver.http.tokens.HttpMethod;
import org.a7fa7fa.httpserver.http.tokens.HttpStatusCode;
import org.a7fa7fa.httpserver.staticcontent.Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Random;

public class DefaultApiEndpoint implements Controller {
    private final static Logger LOGGER = LoggerFactory.getLogger(DefaultApiEndpoint.class);

    @RegisterFunction(targetMethod = HttpMethod.GET, target = "/config")
    public static void myStaticFunction(Context context) throws HttpParsingException, IOException {
        byte[] fileContent = context.readContentFromFile("src/main/resources/http.json");
        context.setResponse(fileContent);
        context.setDefaultResponseHeader();
        context.setResponseStatus(HttpStatusCode.SUCCESSFUL_RESPONSE_200_OK);
        context.send();
        LOGGER.info("myStaticFunction called with HttpRequest: " + context);
    }

    @RegisterFunction(targetMethod = HttpMethod.GET, target = "/stream-file")
    public static void anotherStaticFunction(Context context) throws IOException, HttpParsingException {
        context.setDefaultResponseHeader();
        context.addHeader(new HttpHeader(HeaderName.TRANSFER_ENCODING, "chunked"));
        context.setResponseStatus(HttpStatusCode.SUCCESSFUL_RESPONSE_200_OK);
        context.sendStatusAndHeader();
        BufferedReader br = Reader.readBufferedFile(Reader.getFilePath("","src/main/resources/public/index.html"));
        context.streamData(br);
    }

    private static String generateRandomString(int targetStringLength) {

        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int j = 0; j < 25; j++) {
            for (int i = 0; i < targetStringLength; i++) {
                int randomLimitedInt = leftLimit + (int)
                        (random.nextFloat() * (rightLimit - leftLimit + 1));
                buffer.append((char) randomLimitedInt);
                if (random.nextFloat() > 0.9) {
                    buffer.append("\r\n");
                }
            }
            buffer.append("\r\n");
        }
        return buffer.toString();
    }

    @RegisterFunction(targetMethod = HttpMethod.GET, target = "/stream")
    public static void yetAnotherStaticFunction(Context context) throws HttpParsingException, IOException, InterruptedException {

        context.setDefaultResponseHeader();
        context.addHeader(new HttpHeader(HeaderName.TRANSFER_ENCODING, "chunked"));
        context.setResponseStatus(HttpStatusCode.SUCCESSFUL_RESPONSE_200_OK);
        context.sendStatusAndHeader();
        LOGGER.info("Body send");

        context.streamData(generateRandomString(80).getBytes());

        LOGGER.info("yetAnotherStaticFunction called with HttpRequest: " + context);
    }

}
