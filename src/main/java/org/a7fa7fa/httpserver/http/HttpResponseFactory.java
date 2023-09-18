package org.a7fa7fa.httpserver.http;

public class HttpResponseFactory {

    public static HttpResponse generateResponseFor(HttpRequest httpRequest, String webroot)  {
        return switch (httpRequest.getMethod()) {
            case GET -> new HttpResponseGET(HttpVersion.HTTP_1_1, HttpStatusCode.SUCCESSFUL_RESPONSE_200_OK, webroot);
            case HEAD -> new HttpResponseHEAD(HttpVersion.HTTP_1_1, HttpStatusCode.SUCCESSFUL_RESPONSE_200_OK, webroot);
        };
    }
}