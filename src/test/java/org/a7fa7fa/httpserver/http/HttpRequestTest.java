package org.a7fa7fa.httpserver.http;

import org.a7fa7fa.httpserver.http.tokens.HeaderName;
import org.a7fa7fa.httpserver.http.tokens.HttpStatusCode;
import org.a7fa7fa.httpserver.http.tokens.HttpVersion;
import org.a7fa7fa.httpserver.parser.ByteProcessor;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;


class HttpRequestTest {

    @Test
    void clientShouldUnderstandsTypeEveryType() {
        HttpRequest request = new HttpRequest();
        assertNotNull(request);
        request.addHeader(new HttpHeader(HeaderName.ACCEPT, "*/*"));
        assertFalse(request.clientNotUnderstandsType("text"));
    }
    @Test
    void clientShouldNotUnderstandTypeBecauseOfMissingHeader() {
        HttpRequest request = new HttpRequest();
        assertNotNull(request);
        assertFalse(request.clientNotUnderstandsType("txt"));
    }
    @Test
    void clientShouldUnderstandTypeTxt() {
        HttpRequest request = new HttpRequest();
        assertNotNull(request);
        request.addHeader(new HttpHeader(HeaderName.ACCEPT, "txt"));
        assertTrue(request.clientNotUnderstandsType("not txt"));
    }
}