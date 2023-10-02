package org.a7fa7fa.httpserver.http;

import org.a7fa7fa.httpserver.config.Configuration;
import org.a7fa7fa.httpserver.http.tokens.HeaderName;
import org.a7fa7fa.httpserver.http.tokens.HttpStatusCode;
import org.a7fa7fa.httpserver.http.tokens.HttpVersion;
import org.a7fa7fa.httpserver.parser.ByteProcessor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ContextTest {

    private Configuration config;

    @BeforeAll
    public void beforeClass(){
        config = new Configuration();
        config.setApiPath("api");
        config.setPort(8080);
        config.setLogLevel("error");
        config.setGzipMinFileSizeKb(5);
    }

    @Test
    void setStatusTest() {
        HttpRequest request = new HttpRequest();
        HttpResponse response = new HttpResponse(HttpVersion.HTTP_1_1);
        OutputStream outputStream = new ByteArrayOutputStream();
        ResponseProcessor rp = new ResponseProcessor(response, outputStream);
        Context context = new Context(request, config, rp);
        context.setResponseStatus(HttpStatusCode.CLIENT_ERROR_500_INTERNAL_SEVER_ERROR);
        assertEquals(context.getHttpResponse().getStatusCode(), HttpStatusCode.CLIENT_ERROR_500_INTERNAL_SEVER_ERROR);
    }

    @Test
    void setResponseTest() {
        HttpRequest request = new HttpRequest();
        HttpResponse response = new HttpResponse(HttpVersion.HTTP_1_1);
        OutputStream outputStream = new ByteArrayOutputStream();
        ResponseProcessor rp = new ResponseProcessor(response, outputStream);
        Context context = new Context(request, config, rp);

        String responseData = "this is the date responded";
        String contentLength = String.valueOf(responseData.length());
        try {
            response.setContentType("text");
            context.setResponse(responseData.getBytes(StandardCharsets.US_ASCII));

            assertEquals(context.getHttpResponse().getContentType(), "text");
            assertEquals(context.getHttpResponse().getHeader(HeaderName.CONTENT_LENGTH).getValue(), contentLength);
        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    void setResponseSetNoContentTypeTest() {
        HttpRequest request = new HttpRequest();
        HttpResponse response = new HttpResponse(HttpVersion.HTTP_1_1);
        OutputStream outputStream = new ByteArrayOutputStream();
        ResponseProcessor rp = new ResponseProcessor(response, outputStream);
        Context context = new Context(request, config, rp);

        String responseData = "this is the date responded";
        String contentLength = String.valueOf(responseData.length());
        try {
            context.setResponse(responseData.getBytes(StandardCharsets.US_ASCII));
            assertEquals(context.getHttpResponse().getHeader(HeaderName.CONTENT_LENGTH).getValue(), contentLength);
        } catch (IOException e) {
            fail(e);
        }
    }

    @Test
    void setResponseCompressingTest() {
        HttpRequest request = new HttpRequest();
        HttpResponse response = new HttpResponse(HttpVersion.HTTP_1_1);
        OutputStream outputStream = new ByteArrayOutputStream();
        ResponseProcessor rp = new ResponseProcessor(response, outputStream);
        Context context = new Context(request, config, rp);

        request.addHeader(new HttpHeader(HeaderName.ACCEPT_ENCODING, "gzip"));

        char[] chars = new char[1024 * config.getGzipMinFileSizeKb() * 2];
        Arrays.fill(chars, 'a');
        String responseData = new String(chars);

        try {
            response.setContentType("text");
            context.setResponse(responseData.getBytes(StandardCharsets.US_ASCII));
            assertEquals(context.getHttpResponse().getHeader(HeaderName.CONTENT_LENGTH).getValue(), "46");
        } catch (IOException e) {
            fail(e);
        }
    }

}