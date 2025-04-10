package org.a7fa7fa.httpserver.http;

import org.a7fa7fa.httpserver.http.tokens.HeaderName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class HttpCookie {

    private final static Logger LOGGER = LoggerFactory.getLogger(Context.class);

    private String key;
    private String value;
    private long timestampMs;

    public HttpCookie(String key, String value, long timestampMs) {
        this.key = key;
        this.value = value;
        this.timestampMs = timestampMs;
    }


    private static final DateTimeFormatter HTTP_DATE_FORMAT = DateTimeFormatter
        .ofPattern("EEE, dd MMM yyyy HH:mm:ss z")
        .withZone(ZoneOffset.UTC);

    public static String extractValue(String keyName, HttpHeader cookieheader) {
        if (cookieheader.getName() != HeaderName.COOKIE.getName()) return null;

        String cookieString = cookieheader.getValue();

        int i = 0;
        char equal = '=';
        char space = ' ';
        char semi = ';';
        StringBuffer keyBuffer = new StringBuffer();
        boolean foundKey = false;
        while (i < cookieString.length()) {
            if (cookieString.charAt(i) == space) {
                i++;
                continue;
            }

            if (cookieString.charAt(i) == equal && !keyBuffer.toString().equals(keyName)) {
                keyBuffer.delete(0, keyBuffer.length());
                while (i < cookieString.length() && cookieString.charAt(i) != semi) {i++;}
                i++;
                continue;
            }

            if (cookieString.charAt(i) == equal && keyBuffer.toString().equals(keyName)) {
                foundKey = true;
                keyBuffer.delete(0, keyBuffer.length());
                i++;
                continue;
            }

            if (!foundKey) {
                keyBuffer.append(cookieString.charAt(i));
            } else {
                if (cookieString.charAt(i) == semi) break;
                keyBuffer.append(cookieString.charAt(i));
            }
            i++;

        }
        LOGGER.debug("cookieString" + cookieString);
        if (foundKey) return keyBuffer.toString();
        return null;
    }

    public static String formatExpires(long timestampMs) {
        ZonedDateTime dateTime = Instant.ofEpochMilli(timestampMs).atZone(ZoneOffset.UTC);
        return "Expires=" + HTTP_DATE_FORMAT.format(dateTime);
    }

    private String stringifyContent() {
        StringBuilder builder = new StringBuilder();
        builder.append(key);
        builder.append("=");
        builder.append(value);
        builder.append("; ");
        builder.append(formatExpires(timestampMs));
        builder.append("; ");
        builder.append("path=/");

        return builder.toString();
    }

    public HttpHeader toHeader() {
        return new HttpHeader(HeaderName.SET_COOKIE, this.stringifyContent());
    }

}

