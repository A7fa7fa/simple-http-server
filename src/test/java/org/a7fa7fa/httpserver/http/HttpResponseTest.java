package org.a7fa7fa.httpserver.http;

import java.nio.charset.StandardCharsets;

import org.a7fa7fa.httpserver.config.Configuration;
import org.a7fa7fa.httpserver.http.tokens.HeaderName;
import org.a7fa7fa.httpserver.http.tokens.HttpStatusCode;
import org.a7fa7fa.httpserver.http.tokens.HttpVersion;
import org.a7fa7fa.httpserver.parser.ByteProcessor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


class HttpResponseTest {
    
    static private Configuration config;

    @BeforeAll
    public static void beforeClass(){
        config = new Configuration();
        config.setApiPath("api");
        config.setPort(8080);
        config.setLogLevel("error");
        config.setGzipMinFileSizeKb(5);
        config.setHost("localhost");
    }
    
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
    void shouldAddHeaderToResponseTest() {
        HttpResponse response = new HttpResponse(HttpVersion.HTTP_1_1);
        response.setStatusCode(HttpStatusCode.SUCCESSFUL_RESPONSE_200_OK);
        assertNotNull(response);
        response.addHeader(new HttpHeader(HeaderName.CONTENT_LENGTH, "123"));
        assertEquals(response.getHttpHeaders(), "content-length: 123\r\n");
        response.addHeader(new HttpHeader(HeaderName.ACCEPT, "45"));
        assertEquals(response.getHttpHeaders(), "content-length: 123\r\naccept: 45\r\n");
    }

    @Test
    void shouldAddDefaultHeaderToResponseTest() {
        HttpResponse response = new HttpResponse(HttpVersion.HTTP_1_1);
        response.setStatusCode(HttpStatusCode.SUCCESSFUL_RESPONSE_200_OK);
        assertNotNull(response);
        response.setDefaultHeader(this.config);
        assertTrue(response.getHttpHeaders().contains("server"));
        assertTrue(response.getHttpHeaders().contains("date"));
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
                "server: servervalue\r\n" +
                "accept: 45\r\n" +
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
                "server: servervalue\r\n" +
                "accept: 45\r\n" +
                "\r\n";
        assertEquals(ByteProcessor.byteToString(response.buildCompleteMessage()), validMessage);
    }

    @Test
    void shouldCreateChunk() {
        String chunkData = "this is chunk data";
        String chunkDataSizeHex = Integer.toHexString(chunkData.length());
        byte[] data = chunkData.getBytes(StandardCharsets.US_ASCII);
        HttpResponse response = new HttpResponse(HttpVersion.HTTP_1_1);
        assertNotNull(response);
        assertEquals(ByteProcessor.byteToString(response.createChunk(data)), chunkDataSizeHex+"\r\n"+chunkData+"\r\n");
    }

    @Test
    void shouldCreateChunkWithSizeAsHex() {
        String chunkData = "this is chunk data. more date. more date. more date. more date. more date.";
        String chunkDataSizeHex = "4a";
        byte[] data = chunkData.getBytes(StandardCharsets.US_ASCII);
        HttpResponse response = new HttpResponse(HttpVersion.HTTP_1_1);
        assertNotNull(response);
        assertTrue(ByteProcessor.byteToString(response.createChunk(data)).startsWith(chunkDataSizeHex));
    }

    @Test
    void shouldReturnCompleteMessageWithoutBody() {
        HttpResponse response = new HttpResponse(HttpVersion.HTTP_1_1);
        assertNotNull(response);

        response.setStatusCode(HttpStatusCode.SUCCESSFUL_RESPONSE_200_OK);
        response.addHeader(new HttpHeader(HeaderName.CONTENT_TYPE, "json"));
        response.setDefaultHeader(this.config);
        HttpHeader dateHeader = new HttpHeader();
        dateHeader.setName("date");
        dateHeader.setValue("123456798");
        response.addHeader(dateHeader);

        byte[] message = response.buildCompleteMessage();

        assertNotNull(message);

        String expected = "HTTP/1.1 200 OK\r\n" +
                "date: 123456798\r\n" +
                "server: simple-http-server\r\n" +
                "host: localhost\r\n" +
                "content-type: json\r\n"+
                "connection: close\r\n" +
                "\r\n";

        assertEquals(ByteProcessor.byteToString(message), expected);
    }

    @Test
    void shouldReturnCompleteMessageWithBody() {
        HttpResponse response = new HttpResponse(HttpVersion.HTTP_1_1);
        assertNotNull(response);

        response.setStatusCode(HttpStatusCode.SUCCESSFUL_RESPONSE_200_OK);
        response.addHeader(new HttpHeader(HeaderName.CONTENT_TYPE, "json"));
        response.setDefaultHeader(this.config);
        HttpHeader dateHeader = new HttpHeader();
        dateHeader.setName("date");
        dateHeader.setValue("123456798");
        response.addHeader(dateHeader);
        String body = "this is some data";
        response.addBody(body.getBytes());

        byte[] message = response.buildCompleteMessage();

        assertNotNull(message);

        String expected = "HTTP/1.1 200 OK\r\n" +
                "date: 123456798\r\n" +
                "server: simple-http-server\r\n" +
                "host: localhost\r\n" +
                "content-type: json\r\n"+
                "connection: close\r\n" +
                "\r\n" +
                "this is some data";

        assertEquals(ByteProcessor.byteToString(message), expected);
    }

    @Test
    void shouldReturnStatusLineWithHeaders() {
        HttpResponse response = new HttpResponse(HttpVersion.HTTP_1_1);
        assertNotNull(response);

        response.setStatusCode(HttpStatusCode.SUCCESSFUL_RESPONSE_200_OK);
        response.addHeader(new HttpHeader(HeaderName.CONTENT_TYPE, "json"));
        response.setDefaultHeader(this.config);
        HttpHeader dateHeader = new HttpHeader();
        dateHeader.setName("date");
        dateHeader.setValue("123456798");
        response.addHeader(dateHeader);

        byte[] message = response.buildStatusWithHeaders();

        assertNotNull(message);

        String expected = "HTTP/1.1 200 OK\r\n" +
                "date: 123456798\r\n" +
                "server: simple-http-server\r\n" +
                "host: localhost\r\n" +
                "content-type: json\r\n"+
                "connection: close\r\n" +
                "\r\n";

        assertEquals(ByteProcessor.byteToString(message), expected);
    }

    @Test
    void shouldReturnHeadersAsString() {
        HttpResponse response = new HttpResponse(HttpVersion.HTTP_1_1);
        assertNotNull(response);

        response.setStatusCode(HttpStatusCode.SUCCESSFUL_RESPONSE_200_OK);
        response.addHeader(new HttpHeader(HeaderName.CONTENT_TYPE, "json"));
        response.setDefaultHeader(this.config);
        HttpHeader dateHeader = new HttpHeader();
        dateHeader.setName("date");
        dateHeader.setValue("123456798");
        response.addHeader(dateHeader);

        String headers = response.getHttpHeaders();

        assertNotNull(headers);

        String expected = "date: 123456798\r\n" +
                "server: simple-http-server\r\n" +
                "host: localhost\r\n" +
                "content-type: json\r\n" +
                "connection: close\r\n";

                assertEquals(headers, expected);
    }


}