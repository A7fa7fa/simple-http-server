package org.a7fa7fa.myserver;
import org.a7fa7fa.httpserver.controller.Controller;
import org.a7fa7fa.httpserver.controller.RegisterFunction;
import org.a7fa7fa.httpserver.http.Context;
import org.a7fa7fa.httpserver.http.HttpHeader;
import org.a7fa7fa.httpserver.http.exceptions.ClientDisconnectException;
import org.a7fa7fa.httpserver.http.exceptions.HttpParsingException;
import org.a7fa7fa.httpserver.http.tokens.HeaderName;
import org.a7fa7fa.httpserver.http.tokens.HttpMethod;
import org.a7fa7fa.httpserver.http.tokens.HttpStatusCode;
import org.a7fa7fa.httpserver.staticcontent.Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

public class DefaultApiEndpoint implements Controller {
    private final static Logger LOGGER = LoggerFactory.getLogger(DefaultApiEndpoint.class);

    @RegisterFunction(targetMethod = HttpMethod.GET, target = "/config")
    public static void myStaticFunction(Context context) throws HttpParsingException, IOException, ClientDisconnectException {
        byte[] fileContent = context.readContentFromFile(context.getConfiguration().getFileLocation());
        context.setResponse(fileContent);
        context.setDefaultResponseHeader();
        context.setResponseStatus(HttpStatusCode.SUCCESSFUL_RESPONSE_200_OK);
        context.send();
        LOGGER.debug("myStaticFunction called with HttpRequest: " + context);
    }

    @RegisterFunction(targetMethod = HttpMethod.GET, target = "/stream-file")
    public static void anotherStaticFunction(Context context) throws IOException, ClientDisconnectException {
        context.setDefaultResponseHeader();
        context.addHeader(new HttpHeader(HeaderName.TRANSFER_ENCODING, "chunked"));
        context.setResponseStatus(HttpStatusCode.SUCCESSFUL_RESPONSE_200_OK);
        context.sendStatusAndHeader();
        FileInputStream fileInputStream = Reader.readStreamFile("src/main/resources/public/index.html");
        context.streamData(fileInputStream);
    }


    private static String generateRandomString(int targetStringLength) {

        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
            if (random.nextFloat() > 0.9) {
                buffer.append("\r\n");
            }
        }

        return buffer.toString();
    }

    @RegisterFunction(targetMethod = HttpMethod.GET, target = "/stream")
    public static void yetAnotherStaticFunction(Context context) throws ClientDisconnectException, IOException {

        context.setDefaultResponseHeader();
        context.addHeader(new HttpHeader(HeaderName.TRANSFER_ENCODING, "chunked"));
        context.setResponseStatus(HttpStatusCode.SUCCESSFUL_RESPONSE_200_OK);
        context.sendStatusAndHeader();
        LOGGER.debug("Body send");

        context.streamData(generateRandomString(80).getBytes());

        LOGGER.debug("yetAnotherStaticFunction called with HttpRequest: " + context);
    }

    @RegisterFunction(targetMethod = HttpMethod.GET, target = "/stream-random")
    public static void randomStream(Context context) throws InterruptedException, ClientDisconnectException {

        context.setDefaultResponseHeader();
        context.addHeader(new HttpHeader(HeaderName.TRANSFER_ENCODING, "chunked"));
        context.setResponseStatus(HttpStatusCode.SUCCESSFUL_RESPONSE_200_OK);
        context.sendStatusAndHeader();
        LOGGER.debug("Body send");

        int runForSec = 60;

        long start = System.currentTimeMillis();

        while (System.currentTimeMillis() - start < 1000 * runForSec ) {
            context.sendAsChunk(DefaultApiEndpoint.generateRandomString(1).getBytes());
            Thread.sleep(300);
        }

        context.endStream();

        LOGGER.debug("yetAnotherStaticFunction called with HttpRequest: " + context);
    }

}
