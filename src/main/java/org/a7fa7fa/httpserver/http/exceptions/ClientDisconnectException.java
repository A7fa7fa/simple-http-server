package org.a7fa7fa.httpserver.http.exceptions;

public class ClientDisconnectException extends Exception {

    public ClientDisconnectException(Exception e) {
        super(e);
    }

    public ClientDisconnectException() {
        super();
    }

}
