package org.a7fa7fa.httpserver.controller;

import org.a7fa7fa.httpserver.http.Context;
import org.a7fa7fa.httpserver.http.exceptions.ClientDisconnectException;
import org.a7fa7fa.httpserver.http.exceptions.HttpParsingException;
import org.a7fa7fa.httpserver.http.tokens.HttpMethod;
import org.a7fa7fa.httpserver.http.tokens.HttpStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class StaticSite implements Controller {

    private final static Logger LOGGER = LoggerFactory.getLogger(StaticSite.class);

    @RegisterFunction(targetMethod = HttpMethod.GET, target = "*", controllerType = ControllerType.STATIC)
    public static void getStaticSite(Context context) throws HttpParsingException, IOException, ClientDisconnectException {
        byte[] fileContent = context.readTargetFromFile();
        context.setResponse(fileContent);
        context.setDefaultResponseHeader();
        context.setResponseStatus(HttpStatusCode.SUCCESSFUL_RESPONSE_200_OK);
        context.send();
        LOGGER.debug("Site loaded : " + context.toString());
    }

    @RegisterFunction(targetMethod = HttpMethod.HEAD, target = "*", controllerType = ControllerType.STATIC)
    public static void headStaticSite(Context context) throws HttpParsingException, IOException, ClientDisconnectException {
        byte[] fileContent = context.readTargetFromFile();
        context.setResponse(fileContent);
        context.setDefaultResponseHeader();
        context.setResponseStatus(HttpStatusCode.SUCCESSFUL_RESPONSE_200_OK);
        context.sendStatusAndHeader();

        LOGGER.debug("Site loaded : " + context.toString());
    }
}