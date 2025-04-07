package org.a7fa7fa.httpserver.http.tokens;

public enum HttpStatusCode {

    /* CLIENT ERRORS*/
    CLIENT_ERROR_400_BAD_REQUEST(400, "Bad Request"),
    CLIENT_ERROR_401_UNAUTHORIZED(401, "Unauthorized"),
    CLIENT_ERROR_402_FORBIDDEN(403, "Forbidden"),
    CLIENT_ERROR_404_NOT_FOUND(404, "Not found"),
    CLIENT_ERROR_405_METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    CLIENT_ERROR_414_URI_TOO_LONG(414, "URI too long"),
    CLIENT_ERROR_415_UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
    CLIENT_ERROR_422_UNPROCESSABLE_CONTENT(422, "Unprocessable Content"),

    /* SERVER ERRORS*/
    CLIENT_ERROR_500_INTERNAL_SEVER_ERROR(500, "Internal Server Error"),
    CLIENT_ERROR_501_NOT_IMPLEMENTED(501, "Not implemented"),
    CLIENT_ERROR_505_HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported"),

    /* Successful responses*/
    SUCCESSFUL_RESPONSE_200_OK(200, "OK");

    public final int STATUS_CODE;
    public final String MESSAGE;

    HttpStatusCode(int STATUS_CODE, String MESSAGE) {
        this.STATUS_CODE = STATUS_CODE;
        this.MESSAGE = MESSAGE;
    }
}
