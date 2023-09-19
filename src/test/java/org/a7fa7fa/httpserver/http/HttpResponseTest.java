package org.a7fa7fa.httpserver.http;

import org.a7fa7fa.httpserver.http.tokens.HeaderName;
import org.a7fa7fa.httpserver.http.tokens.HttpStatusCode;
import org.a7fa7fa.httpserver.http.tokens.HttpVersion;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;


class HttpResponseTest {
    @Test
    void getStatusLineOkTest() {
        HttpResponse response = new HttpResponse(HttpVersion.HTTP_1_1, HttpStatusCode.SUCCESSFUL_RESPONSE_200_OK);
        assertNotNull(response);
        assertEquals(response.getStatusLine(), "HTTP/1.1 200 OK");
    }
    @Test
    void getStatusLineNotOkTest() {
        HttpResponse response = new HttpResponse(HttpVersion.HTTP_1_1, HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        assertNotNull(response);
        assertEquals(response.getStatusLine(), "HTTP/1.1 400 Bad Request");
    }

    @Test
    void addHeaderTest() {
        HttpResponse response = new HttpResponse(HttpVersion.HTTP_1_1, HttpStatusCode.SUCCESSFUL_RESPONSE_200_OK);
        assertNotNull(response);
        response.addHeader(new HttpHeader(HeaderName.CONTENT_LENGTH, "123"));
        assertEquals(response.getHeaders(), "content-length: 123\r\n");
        response.addHeader(new HttpHeader(HeaderName.ACCEPT, "45"));
        assertEquals(response.getHeaders(), "content-length: 123\r\naccept: 45\r\n");
    }

    @Test
    void addDefaultHeaderTest() {
        HttpResponse response = new HttpResponse(HttpVersion.HTTP_1_1, HttpStatusCode.SUCCESSFUL_RESPONSE_200_OK);
        assertNotNull(response);
        response.addDefaultHeader();
        assertTrue(response.getHeaders().contains("server"));
        assertTrue(response.getHeaders().contains("date"));
    }

    @Test
    void shouldBuildValidResponseWithBodyMessage() {
        HttpResponse response = new HttpResponse(HttpVersion.HTTP_1_1, HttpStatusCode.SUCCESSFUL_RESPONSE_200_OK);
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
        assertEquals(HttpResponse.byteToString(response.getBytes()), validMessage);
    }
    @Test
    void shouldBuildValidResponseWithoutBodyMessage() {
        HttpResponse response = new HttpResponse(HttpVersion.HTTP_1_1, HttpStatusCode.SUCCESSFUL_RESPONSE_200_OK);
        assertNotNull(response);
        response.addHeader(new HttpHeader(HeaderName.CONTENT_LENGTH, "123"));
        response.addHeader(new HttpHeader(HeaderName.ACCEPT, "45"));
        response.addHeader(new HttpHeader(HeaderName.SERVER, "servervalue"));

        String validMessage = "HTTP/1.1 200 OK\r\n" +
                "content-length: 123\r\n" +
                "accept: 45\r\n" +
                "server: servervalue\r\n" +
                "\r\n";
        assertEquals(HttpResponse.byteToString(response.getBytes()), validMessage);
    }
    @Test
    void clientUnderstandsTypeEveryType() {
        HttpRequest request = new HttpRequest();
        request.addHeader(new HttpHeader(HeaderName.ACCEPT, "*/*"));
        HttpResponse response = new HttpResponse(HttpVersion.HTTP_1_1, HttpStatusCode.SUCCESSFUL_RESPONSE_200_OK);
        assertNotNull(response);
        assertFalse(response.clientNotUnderstandsType(request, "txt"));
    }
    @Test
    void clientUnderstandsTypeMissingHeader() {
        HttpRequest request = new HttpRequest();
        HttpResponse response = new HttpResponse(HttpVersion.HTTP_1_1, HttpStatusCode.SUCCESSFUL_RESPONSE_200_OK);
        assertNotNull(response);
        assertFalse(response.clientNotUnderstandsType(request, "txt"));
    }
    @Test
    void clientUnderstandsTypeTxt() {
        HttpRequest request = new HttpRequest();
        request.addHeader(new HttpHeader(HeaderName.ACCEPT, "txt"));
        HttpResponse response = new HttpResponse(HttpVersion.HTTP_1_1, HttpStatusCode.SUCCESSFUL_RESPONSE_200_OK);
        assertNotNull(response);
        assertTrue(response.clientNotUnderstandsType(request, "not txt"));
    }

    @Test
    void concatBytesTest() {
        String firstLine = "this is a line";
        String secondLine = "this is another line";

        byte[] concatenated = HttpResponse.concatResponse(firstLine.getBytes(StandardCharsets.US_ASCII), secondLine.getBytes(StandardCharsets.UTF_8));

        HttpResponse.byteToString(concatenated);
        assertEquals(HttpResponse.byteToString(concatenated), firstLine + secondLine);
    }

}