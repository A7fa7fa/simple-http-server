package org.a7fa7fa.httpserver.config;

public class HttpConfigurationException extends RuntimeException {
    public HttpConfigurationException() {
        super();
    }

    public HttpConfigurationException(String message) {
        super(message);
    }

    public HttpConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpConfigurationException(Throwable cause) {
        super(cause);
    }

}
