package org.a7fa7fa.httserver.http;

public enum HttpMethod {
    GET, HEAD;
    public static final int MAX_LENGTH;

    static {
        int tempMaxValue = -1;

        for (HttpMethod method: values()) {
            tempMaxValue = Math.max(method.name().length(), tempMaxValue);
        }

        MAX_LENGTH = tempMaxValue;
    }
}
