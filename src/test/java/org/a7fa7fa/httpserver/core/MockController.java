package org.a7fa7fa.httpserver.core;

import org.a7fa7fa.httpserver.controller.Controller;
import org.a7fa7fa.httpserver.controller.ControllerType;
import org.a7fa7fa.httpserver.controller.RegisterFunction;
import org.a7fa7fa.httpserver.http.ClientDisconnectException;
import org.a7fa7fa.httpserver.http.Context;
import org.a7fa7fa.httpserver.http.HttpHeader;
import org.a7fa7fa.httpserver.http.tokens.HeaderName;
import org.a7fa7fa.httpserver.http.tokens.HttpMethod;
import org.a7fa7fa.httpserver.http.tokens.HttpStatusCode;

import java.io.IOException;

public class MockController implements Controller {
    @RegisterFunction(targetMethod = HttpMethod.GET, target = "*", controllerType = ControllerType.STATIC)
    public static void getStaticSite(Context context) throws IOException, ClientDisconnectException {
        context.setResponse("this is the file returned".getBytes());
        context.addHeader(new HttpHeader(HeaderName.CONTENT_TYPE, "text"));
        context.setDefaultResponseHeader();
        context.addHeader(new HttpHeader(HeaderName.DATE, "12345"));
        context.setResponseStatus(HttpStatusCode.SUCCESSFUL_RESPONSE_200_OK);
        context.send();
    }
}
