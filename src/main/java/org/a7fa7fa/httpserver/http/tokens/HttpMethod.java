package org.a7fa7fa.httpserver.http.tokens;

public enum HttpMethod {

    GET, HEAD, POST;

    public static final int MAX_LENGTH;

    static {
        int tempMaxValue = -1;

        for (HttpMethod method: values()) {
            tempMaxValue = Math.max(method.name().length(), tempMaxValue);
        }

        MAX_LENGTH = tempMaxValue;
    }
}
