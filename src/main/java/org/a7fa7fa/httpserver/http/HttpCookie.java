package org.a7fa7fa.httpserver.http;

import org.a7fa7fa.httpserver.config.Configuration;
import org.a7fa7fa.httpserver.http.exceptions.ClientDisconnectException;
import org.a7fa7fa.httpserver.http.exceptions.HttpParsingException;
import org.a7fa7fa.httpserver.http.tokens.HeaderName;
import org.a7fa7fa.httpserver.http.tokens.HttpStatusCode;
import org.a7fa7fa.httpserver.parser.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
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

