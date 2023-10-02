package org.a7fa7fa.httpserver.controller;

import org.a7fa7fa.httpserver.http.ClientDisconnectException;
import org.a7fa7fa.httpserver.http.Context;
import org.a7fa7fa.httpserver.http.HttpParsingException;
import org.a7fa7fa.httpserver.http.tokens.HttpMethod;
import org.a7fa7fa.httpserver.http.tokens.HttpStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class StaticSite implements Controller {
    private final static Logger LOGGER = LoggerFactory.getLogger(StaticSite.class);

    @RegisterFunction(targetMethod = HttpMethod.GET, target = "*", controllerType = ControllerType.STATIC)
    public static void getStaticSite(Context context) throws HttpParsingException, IOException, ClientDisconnectException {
        context.setResponse("content".getBytes(StandardCharsets.US_ASCII));
        context.setDefaultResponseHeader();
        context.setResponseStatus(HttpStatusCode.SUCCESSFUL_RESPONSE_200_OK);
        context.send();
        LOGGER.debug("Site loaded : " + context.toString());
    }
}
