package org.a7fa7fa.httpserver.http;

import org.a7fa7fa.httpserver.http.tokens.HeaderName;
import org.a7fa7fa.httpserver.http.tokens.HttpStatusCode;
import org.a7fa7fa.httpserver.http.tokens.HttpVersion;
import org.a7fa7fa.httpserver.parser.ByteProcessor;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;


class HttpResponseTest {
    @Test
    void getStatusLineOkTest() {
        HttpResponse response = new HttpResponse(HttpVersion.HTTP_1_1);
        response.setStatusCode(HttpStatusCode.SUCCESSFUL_RESPONSE_200_OK);
        assertNotNull(response);
        assertEquals(response.getStatusLine(), "HTTP/1.1 200 OK");
    }
    @Test
    void getStatusLineNotOkTest() {
        HttpResponse response = new HttpResponse(HttpVersion.HTTP_1_1);
        response.setStatusCode(HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        assertNotNull(response);
        assertEquals(response.getStatusLine(), "HTTP/1.1 400 Bad Request");
    }

    @Test
    void addHeaderTest() {
        HttpResponse response = new HttpResponse(HttpVersion.HTTP_1_1);
        response.setStatusCode(HttpStatusCode.SUCCESSFUL_RESPONSE_200_OK);
        assertNotNull(response);
        response.addHeader(new HttpHeader(HeaderName.CONTENT_LENGTH, "123"));
        assertEquals(response.getHeaders(), "content-length: 123\r\n");
        response.addHeader(new HttpHeader(HeaderName.ACCEPT, "45"));
        assertEquals(response.getHeaders(), "content-length: 123\r\naccept: 45\r\n");
    }

    @Test
    void addDefaultHeaderTest() {
        HttpResponse response = new HttpResponse(HttpVersion.HTTP_1_1);
        response.setStatusCode(HttpStatusCode.SUCCESSFUL_RESPONSE_200_OK);
        assertNotNull(response);
        response.setDefaultHeader();
        assertTrue(response.getHeaders().contains("server"));
        assertTrue(response.getHeaders().contains("date"));
    }

    @Test
    void shouldBuildValidResponseWithBodyMessage() {
        HttpResponse response = new HttpResponse(HttpVersion.HTTP_1_1);
        response.setStatusCode(HttpStatusCode.SUCCESSFUL_RESPONSE_200_OK);
        assertNotNull(response);
        response.addHeader(new HttpHeader(HeaderName.CONTENT_LENGTH, "123"));
        response.addHeader(new HttpHeader(HeaderName.ACCEPT, "45"));
        response.addHeader(new HttpHeader(HeaderName.SERVER, "servervalue"));
        String body = "{\"key\": \"value\"}";
        response.addBody(body.getBytes(StandardCharsets.US_ASCII));

        String validMessage = "HTTP/1.1 200 OK\r\n" +
                "content-length: 123\r\n" +
                "accept: 45\r\n" +
                "server: servervalue\r\n" +
                "\r\n" +
                "{\"key\": \"value\"}";
        assertEquals(ByteProcessor.byteToString(response.buildCompleteMessage()), validMessage);
    }
    @Test
    void shouldBuildValidResponseWithoutBodyMessage() {
        HttpResponse response = new HttpResponse(HttpVersion.HTTP_1_1);
        response.setStatusCode(HttpStatusCode.SUCCESSFUL_RESPONSE_200_OK);
        assertNotNull(response);
        response.addHeader(new HttpHeader(HeaderName.CONTENT_LENGTH, "123"));
        response.addHeader(new HttpHeader(HeaderName.ACCEPT, "45"));
        response.addHeader(new HttpHeader(HeaderName.SERVER, "servervalue"));

        String validMessage = "HTTP/1.1 200 OK\r\n" +
                "content-length: 123\r\n" +
                "accept: 45\r\n" +
                "server: servervalue\r\n" +
                "\r\n";
        assertEquals(ByteProcessor.byteToString(response.buildCompleteMessage()), validMessage);
    }
    @Test
    void clientUnderstandsTypeEveryType() {
        HttpRequest request = new HttpRequest();
        assertNotNull(request);
        request.addHeader(new HttpHeader(HeaderName.ACCEPT, "*/*"));
        assertFalse(request.clientNotUnderstandsType("text"));
    }
    @Test
    void clientUnderstandsTypeMissingHeader() {
        HttpRequest request = new HttpRequest();
        assertNotNull(request);
        assertFalse(request.clientNotUnderstandsType("txt"));
    }
    @Test
    void clientUnderstandsTypeTxt() {
        HttpRequest request = new HttpRequest();
        assertNotNull(request);
        request.addHeader(new HttpHeader(HeaderName.ACCEPT, "txt"));
        assertTrue(request.clientNotUnderstandsType("not txt"));
    }

}