package org.a7fa7fa.httpserver.http;

import org.a7fa7fa.httpserver.config.Configuration;
import org.a7fa7fa.httpserver.http.tokens.HeaderName;
import org.a7fa7fa.httpserver.http.tokens.HttpStatusCode;
import org.a7fa7fa.httpserver.http.tokens.HttpVersion;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ResponseProcessorTest {

    @Test
    void setContentTypeTest() {
        HttpResponse response = new HttpResponse(HttpVersion.HTTP_1_1);
        OutputStream outputStream = new ByteArrayOutputStream();
        ResponseProcessor responseProcessor = new ResponseProcessor(response, outputStream);

        HttpRequest request = new HttpRequest();
        request.addHeader(new HttpHeader(HeaderName.ACCEPT, "text"));

        try {
            responseProcessor.setContentTypeIfClientUnderstands(request, "text");
            HttpHeader header = responseProcessor.getResponse().getHeader(HeaderName.CONTENT_TYPE);
            assertNotNull(header);
            assertEquals(header.getValue(), "text");
        } catch (HttpParsingException e) {
            fail(e);
        }
    }

    @Test
    void setContentTypeWildcardTest() {
        HttpResponse response = new HttpResponse(HttpVersion.HTTP_1_1);
        OutputStream outputStream = new ByteArrayOutputStream();
        ResponseProcessor responseProcessor = new ResponseProcessor(response, outputStream);

        HttpRequest request = new HttpRequest();
        request.addHeader(new HttpHeader(HeaderName.ACCEPT, "*/*"));

        try {
            responseProcessor.setContentTypeIfClientUnderstands(request, "text");
            HttpHeader header = responseProcessor.getResponse().getHeader(HeaderName.CONTENT_TYPE);
            assertNotNull(header);
            assertEquals(header.getValue(), "text");
        } catch (HttpParsingException e) {
            fail(e);
        }
    }

    @Test
    void setContentTypeNullTest() {
        HttpResponse response = new HttpResponse(HttpVersion.HTTP_1_1);
        OutputStream outputStream = new ByteArrayOutputStream();
        ResponseProcessor responseProcessor = new ResponseProcessor(response, outputStream);

        HttpRequest request = new HttpRequest();
        request.addHeader(new HttpHeader(HeaderName.ACCEPT, "*/*"));

        try {
            String contentType = null;
            responseProcessor.setContentTypeIfClientUnderstands(request, contentType);
            HttpHeader header = responseProcessor.getResponse().getHeader(HeaderName.CONTENT_TYPE);
            assertNull(header);
        } catch (HttpParsingException e) {
            fail(e);
        }
    }

    @Test
    void shouldThrowClientErrorTest() {
        HttpResponse response = new HttpResponse(HttpVersion.HTTP_1_1);
        OutputStream outputStream = new ByteArrayOutputStream();
        ResponseProcessor responseProcessor = new ResponseProcessor(response, outputStream);

        HttpRequest request = new HttpRequest();
        request.addHeader(new HttpHeader(HeaderName.ACCEPT, "json"));

        try {
            responseProcessor.setContentTypeIfClientUnderstands(request, "text");
            fail();
        } catch (HttpParsingException e) {
            assertEquals(e.errorCode, HttpStatusCode.CLIENT_ERROR_415_UNSUPPORTED_MEDIA_TYPE);
        }
    }
}