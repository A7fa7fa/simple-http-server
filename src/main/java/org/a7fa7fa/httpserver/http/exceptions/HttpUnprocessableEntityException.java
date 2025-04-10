package org.a7fa7fa.httpserver.http.exceptions;

import org.a7fa7fa.httpserver.http.tokens.HttpStatusCode;

public class HttpUnprocessableEntityException extends HttpException {

    public HttpUnprocessableEntityException() {
        super(HttpStatusCode.CLIENT_ERROR_422_UNPROCESSABLE_CONTENT);
    }


}
