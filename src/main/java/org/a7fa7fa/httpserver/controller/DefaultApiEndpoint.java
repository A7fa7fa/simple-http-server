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

import java.io.IOException;
import java.nio.file.Path;

public class DefaultApiEndpoint implements Controller {
    private final static Logger LOGGER = LoggerFactory.getLogger(DefaultApiEndpoint.class);

    @RegisterFunction(targetMethod = HttpMethod.GET, target = "/config")
    public static void myStaticFunction(Context context) throws HttpParsingException, IOException {
        Path filePath = Reader.getFilePath("","src/main/resources/http.json");
        String contentType = Reader.probeContentType(filePath);

        if (contentType != null) {
            if (context.getHttpRequest().clientNotUnderstandsType(contentType)) {
                throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_415_UNSUPPORTED_MEDIA_TYPE);
            }
            context.getHttpResponse().addHeader(new HttpHeader(HeaderName.CONTENT_TYPE, contentType));
        }

        byte[] body = Reader.readFile(filePath);
        context.addBodyToResponse(body);
        context.addDefaultResponseHeader();
        context.setResponseStatus(HttpStatusCode.SUCCESSFUL_RESPONSE_200_OK);
        LOGGER.info("myStaticFunction called with HttpRequest: " + context);
    }

    public static void anotherStaticFunction(Context context) {
        LOGGER.info("anotherStaticFunction called with HttpRequest: " + context);
    }

    @RegisterFunction(targetMethod = HttpMethod.GET, target = "/resource")
    public static void yetAnotherStaticFunction(Context context) throws HttpParsingException {
        LOGGER.info("yetAnotherStaticFunction called with HttpRequest: " + context);
        throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_501_NOT_IMPLEMENTED);
    }

}
