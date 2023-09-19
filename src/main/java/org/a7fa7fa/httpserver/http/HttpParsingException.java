package org.a7fa7fa.httpserver.http;

import org.a7fa7fa.httpserver.http.tokens.HttpStatusCode;

public class HttpParsingException extends Exception {
    public final HttpStatusCode errorCode;

    public HttpParsingException(HttpStatusCode errorCode) {
        super(errorCode.MESSAGE);
        this.errorCode = errorCode;
    }

    public HttpStatusCode getErrorCode() {
        return errorCode;
    }

}
