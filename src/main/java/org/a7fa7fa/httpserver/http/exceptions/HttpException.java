package org.a7fa7fa.httpserver.http.exceptions;

import org.a7fa7fa.httpserver.http.tokens.HttpStatusCode;

public class HttpException extends Exception {

    public final HttpStatusCode errorCode;

    public HttpException(HttpStatusCode errorCode) {
        super(errorCode.MESSAGE);
        this.errorCode = errorCode;
    }

    public HttpStatusCode getErrorCode() {
        return errorCode;
    }

}
