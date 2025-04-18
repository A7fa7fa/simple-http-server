package org.a7fa7fa.httpserver.http;

import org.a7fa7fa.httpserver.http.exceptions.HttpParsingException;
import org.a7fa7fa.httpserver.http.tokens.HeaderName;
import org.a7fa7fa.httpserver.http.tokens.HttpMethod;
import org.a7fa7fa.httpserver.http.tokens.HttpStatusCode;
import org.a7fa7fa.httpserver.http.tokens.HttpVersion;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HttpParserTest {

    private HttpParser httpParser;

    @BeforeAll
    public void beforeClass(){
        httpParser = new HttpParser();
    }
    @Test
    void parseHttpRequest() {
        HttpRequest httpRequest = null;
        try {
            httpRequest = httpParser.parseHttpRequest(generateValidTestCase(), 1024);
        } catch (HttpParsingException e) {
            fail(e);
        }
        assertNotNull(httpRequest);
        assertEquals(httpRequest.getMethod(), HttpMethod.GET);
        assertEquals(httpRequest.getRequestTarget(), "/");
        assertEquals(httpRequest.getOriginalHttpVersion(), "HTTP/1.1");
        assertEquals(httpRequest.getBestCompatibleHttpVersion(), HttpVersion.HTTP_1_1);
        assertEquals(httpRequest.getHeader(HeaderName.CONTENT_LENGTH).getValue(), "14");
        assertEquals(httpRequest.getBody(), "body1\r\nbody2\r\n");
    }

    @Test
    void parseHttpRequestHeaderValueCheckCaseSensitive() {
        HttpRequest httpRequest = null;
        try {
            String rawData = "GET / HTTP/1.1\r\n" +
                    "Host: localhost:8080\r\n" +
                    "Cookie: lowercase+UPPERCASE\r\n" +
                    "Content-Length: 14\r\n" +
                    "\r\n" +
                    "body1\r\n" +
                    "body2\r\n" +
                    "\r\n";
            InputStream inputStream = new ByteArrayInputStream(rawData.getBytes(StandardCharsets.US_ASCII));

            httpRequest = httpParser.parseHttpRequest(inputStream, 1024);
        } catch (HttpParsingException e) {
            fail(e);
        }
        assertNotNull(httpRequest);
        assertEquals(httpRequest.getHeader(HeaderName.COOKIE).getValue(), "lowercase+UPPERCASE");
    }

    @Test
    void parseHttpRequestNoClosingLineBreak() {
        HttpRequest httpRequest = null;
        try {
            httpRequest = httpParser.parseHttpRequest(generateValidTestNoClosingLinebreakCase(), 1024);
        } catch (HttpParsingException e) {
            fail(e);
        }
        assertNotNull(httpRequest);
        assertEquals(httpRequest.getMethod(), HttpMethod.GET);
        assertEquals(httpRequest.getRequestTarget(), "/");
        assertEquals(httpRequest.getOriginalHttpVersion(), "HTTP/1.1");
        assertEquals(httpRequest.getBestCompatibleHttpVersion(), HttpVersion.HTTP_1_1);
        assertEquals(httpRequest.getHeader(HeaderName.CONTENT_LENGTH).getValue(), "14");
    }
    @Test
    void parseHttpRequestBadMethod() {
        HttpRequest httpRequest = null;
        try {
            httpRequest = httpParser.parseHttpRequest(generateBadTestCase(), 1024);
            fail();
        } catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCode.CLIENT_ERROR_501_NOT_IMPLEMENTED);
        }
    }

    @Test
    void parseHttpRequestTooLongMethod() {
        HttpRequest httpRequest = null;
        try {
            httpRequest = httpParser.parseHttpRequest(generateTooLongMethodTestCase(), 1024);
            fail();
        } catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCode.CLIENT_ERROR_501_NOT_IMPLEMENTED);
        }
    }

    @Test
    void parseHttpRequestTooManyArguments() {
        HttpRequest httpRequest = null;
        try {
            httpRequest = httpParser.parseHttpRequest(generateTooManyArgumentsTestCase(), 1024);
            fail();
        } catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }
    }
    @Test
    void parseHttpRequestTooFewArguments() {
        HttpRequest httpRequest = null;
        try {
            httpRequest = httpParser.parseHttpRequest(generateTooFewArgumentsTestCase(), 1024);
            fail();
        } catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }
    }

    @Test
    void parseHttpRequestInvalidCharacterInHeader() {
        HttpRequest httpRequest = null;
        try {
            httpRequest = httpParser.parseHttpRequest(generateHeaderWithInvalidCharacter(), 1024);
            fail();
        } catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }
    }

    @Test
    void parseHttpRequestEmptyPrecedingReqLine() {
        HttpRequest httpRequest = null;
        try {
            httpRequest = httpParser.parseHttpRequest(generatePrecedingEmptyLineTestCase(), 1024);
            assertNotNull(httpRequest);
            assertEquals(httpRequest.getMethod(), HttpMethod.GET);
            assertEquals(httpRequest.getRequestTarget(), "/");
            assertEquals(httpRequest.getOriginalHttpVersion(), "HTTP/1.1");
            assertEquals(httpRequest.getBestCompatibleHttpVersion(), HttpVersion.HTTP_1_1);

        } catch (HttpParsingException e) {
            fail(e);

        }
    }

    @Test
    void parseHttpRequestEmptyReqLine() {
        HttpRequest httpRequest = null;
        try {
            httpRequest = httpParser.parseHttpRequest(generateEmptyReqlineTestCase(), 1024);
            fail();
        } catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCode.CLIENT_ERROR_501_NOT_IMPLEMENTED);
        }
    }
    @Test
    void parseHttpRequestInvalidCRWithoutLF() {
        HttpRequest httpRequest = null;
        try {
            httpRequest = httpParser.parseHttpRequest(generateInvalidCRWithoutLF(), 1024);
            fail();
        } catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }
    }

    @Test
    void parseHttpRequestBadHttpVersionFormat() {
        HttpRequest httpRequest = null;
        try {
            httpRequest = httpParser.parseHttpRequest(generateBadHttpVersionFormat(), 1024);
            fail();
        } catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }
    }
    @Test
    void parseHttpRequestUnsupportedHttpVersionFormat() {
        HttpRequest httpRequest = null;
        try {
            httpRequest = httpParser.parseHttpRequest(generateUnsupportedHttpVersionFormat(), 1024);
            fail();
        } catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCode.CLIENT_ERROR_505_HTTP_VERSION_NOT_SUPPORTED);
        }
    }

    @Test
    void parseHttpRequestSupportedHttpVersionFormat() {
        HttpRequest httpRequest = null;
        try {
            httpRequest = httpParser.parseHttpRequest(generateSupportedHttpVersionFormat(), 1024);
            assertNotNull(httpRequest);
            assertEquals(httpRequest.getBestCompatibleHttpVersion(), HttpVersion.HTTP_1_1);
        } catch (HttpParsingException e) {
            fail(e);
        }
    }

    @Test
    void parseSpaceBeforeColonHeaderTestCase() {
        HttpRequest httpRequest = null;
        try {
            httpRequest = httpParser.parseHttpRequest(generateSpaceBeforeColonHeaderTestCase(), 1024);
            fail();
        } catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }
    }
    @Test
    void parseSpaceInHeaderNameTestCase() {
        HttpRequest httpRequest = null;
        try {
            httpRequest = httpParser.parseHttpRequest(generateSpaceInHeaderNameTestCase(), 1024);
            fail();
        } catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }
    }
    @Test
    void parseQuotedHeaderTestCase() {
        HttpRequest httpRequest = null;
        try {
            httpRequest = httpParser.parseHttpRequest(generateQuotedHeaderTestCase(), 1024);
            assertEquals(httpRequest.getHeaders().get("host").getValue(), "\"localhost:8080\"");
        } catch (HttpParsingException e) {
            fail(e);
        }
    }

    @Test
    void parseQuotedHeaderWithoutClosingQuotTestCase() {
        HttpRequest httpRequest = null;
        try {
            httpRequest = httpParser.parseHttpRequest(generateQuotedHeaderWithoutClosingQuoteTestCase(), 1024);
            fail();
        } catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }
    }
    @Test
    void parseQuotedHeaderWithoutWithNewLineTestCase() {
        HttpRequest httpRequest = null;
        try {
            httpRequest = httpParser.parseHttpRequest(generateQuotedHeaderWithNewLineTestCase(), 1024);
            assertEquals(httpRequest.getHeaders().get("host").getValue(), "\"localhost \r\n :8080\"");
        } catch (HttpParsingException e) {
            fail(e);
        }
    }

    @Test
    void parseHeaderKnownHeaderTestCase() {
        HttpRequest httpRequest = null;
        try {
            httpRequest = httpParser.parseHttpRequest(generateValidTestCase(), 1024);
            assertEquals(httpRequest.getHeaders().get("accept").getValue(), "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
            assertEquals(httpRequest.getHeaders().get("accept").getHeaderField().getName(), "accept");
            assertEquals(httpRequest.getHeaders().get("accept").getHeaderField().getName(), "accept");
        } catch (HttpParsingException e) {
            fail(e);
        }
    }
    @Test
    void parseHeaderGetByEnumTestCase() {
        HttpRequest httpRequest = null;
        try {
            httpRequest = httpParser.parseHttpRequest(generateValidTestCase(), 1024);
            assertEquals(httpRequest.getHeader(HeaderName.ACCEPT).getValue(), "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
            assertEquals(httpRequest.getHeader(HeaderName.ACCEPT).getHeaderField().getName(), "accept");
            assertEquals(httpRequest.getHeader(HeaderName.ACCEPT).getHeaderField().getName(), "accept");
        } catch (HttpParsingException e) {
            fail(e);
        }
    }

    @Test
    void parseHeaderWhitespaceInHeaderNameTestCase() {
        HttpRequest httpRequest = null;
        try {
            httpRequest = httpParser.parseHttpRequest(generateWhitespaceInHeaderNameTestCase(), 1024);
            fail();
        } catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }
    }

    @Test
    void parseBodyNoContentLengthButTransferEncodingTestCase() {
        HttpRequest httpRequest = null;
        try {
            httpRequest = httpParser.parseHttpRequest(generateNoContentLengthHeaderWithTransferEncodingTestCase(), 1024);
            fail();
        } catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCode.CLIENT_ERROR_501_NOT_IMPLEMENTED);
        }
    }

    @Test
    void parseBodyWithContentTestCase() {
        HttpRequest httpRequest = null;
        try {
            httpRequest = httpParser.parseHttpRequest(generateValidBodyTestCase(), 1024);
            assertNotNull(httpRequest);
            assertEquals(httpRequest.getBody(), "body1\r\nbody2\r\n");
        } catch (HttpParsingException e) {
            fail(e);
        }
    }

    @Test
    void parseBodyMaxBodySizeTestCase() {
        HttpRequest httpRequest = null;
        try {
            httpRequest = httpParser.parseHttpRequest(generateValidBodyTestCase(), 2);
            fail();
        } catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }
    }

    @Test
    void parseBodyNoContentLengthNoTransferEncodingTestCase() {
        HttpRequest httpRequest = null;
        try {
            httpRequest = httpParser.parseHttpRequest(generateNoContentLengthHeaderandTransferEncodingTestCase(), 1024);
            fail();
        } catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }
    }
    @Test
    void parseBodyInvalidCharactersInHeader() {
        HttpRequest httpRequest = null;
        try {
            httpRequest = httpParser.parseHttpRequest(generateNoContentLengthHeaderandTransferEncodingTestCase(), 1024);
            fail();
        } catch (HttpParsingException e) {
            assertEquals(e.getErrorCode(), HttpStatusCode.CLIENT_ERROR_400_BAD_REQUEST);
        }
    }



    private InputStream generateValidTestCase() {
        String rawData = "GET / HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "Upgrade-Insecure-Requests: 1\r\n" +
                "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36\r\n" +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\r\n" +
                "Sec-Fetch-Site: none\r\n" +
                "Sec-Fetch-Mode: navigate\r\n" +
                "Accept-Encoding: gzip, deflate, br\r\n" +
                "Accept-Language: en-US,en;q=0.9\r\n" +
                "Content-Length: 14\r\n" +
                "\r\n" +
                "body1\r\n" +
                "body2\r\n" +
                "\r\n";
        InputStream inputStream = new ByteArrayInputStream(rawData.getBytes(StandardCharsets.US_ASCII));
        return inputStream;
    }
    private InputStream generateValidTestNoClosingLinebreakCase() {
        String rawData = "GET / HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "Upgrade-Insecure-Requests: 1\r\n" +
                "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36\r\n" +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\r\n" +
                "Sec-Fetch-Site: none\r\n" +
                "Sec-Fetch-Mode: navigate\r\n" +
                "Accept-Encoding: gzip, deflate, br\r\n" +
                "Accept-Language: en-US,en;q=0.9\r\n" +
                "Content-Length: 14\r\n" +
                "\r\n" +
                "body1\r\n" +
                "body2";
        InputStream inputStream = new ByteArrayInputStream(rawData.getBytes(StandardCharsets.US_ASCII));
        return inputStream;
    }
    private InputStream generateBadTestCase() {
        String rawData = "Get / HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "\r\n";
        InputStream inputStream = new ByteArrayInputStream(rawData.getBytes(StandardCharsets.US_ASCII));
        return inputStream;
    }
    private InputStream generateTooLongMethodTestCase() {
        String rawData = "Geeeeet / HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "\r\n";
        InputStream inputStream = new ByteArrayInputStream(rawData.getBytes(StandardCharsets.US_ASCII));
        return inputStream;
    }
    private InputStream generateTooManyArgumentsTestCase() {
        String rawData = "GET / MANY_ARGUMENTS HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "\r\n";
        InputStream inputStream = new ByteArrayInputStream(rawData.getBytes(StandardCharsets.US_ASCII));
        return inputStream;
    }
    private InputStream generateTooFewArgumentsTestCase() {
        String rawData = "GET /\r\n HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "\r\n";
        InputStream inputStream = new ByteArrayInputStream(rawData.getBytes(StandardCharsets.US_ASCII));
        return inputStream;
    }
    private InputStream generateEmptyReqlineTestCase() {
        String rawData = "\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "\r\n";
        InputStream inputStream = new ByteArrayInputStream(rawData.getBytes(StandardCharsets.US_ASCII));
        return inputStream;
    }

    private InputStream generatePrecedingEmptyLineTestCase() {
        String rawData = "\r\n" +
                "GET / HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "\r\n";
        InputStream inputStream = new ByteArrayInputStream(rawData.getBytes(StandardCharsets.US_ASCII));
        return inputStream;
    }

    private InputStream generateInvalidCRWithoutLF() {
        String rawData = "GET / MANY_ARGUMENTS HTTP/1.1\r" + // <-- no LF
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "\r\n";
        InputStream inputStream = new ByteArrayInputStream(rawData.getBytes(StandardCharsets.US_ASCII));
        return inputStream;
    }

    private InputStream generateBadHttpVersionFormat() {
        String rawData = "GET / http/1.1\r\n" +
                "\r\n";
        InputStream inputStream = new ByteArrayInputStream(rawData.getBytes(StandardCharsets.US_ASCII));
        return inputStream;
    }
    private InputStream generateUnsupportedHttpVersionFormat() {
        String rawData = "GET / HTTP/2.0\r\n" +
                "\r\n";
        InputStream inputStream = new ByteArrayInputStream(rawData.getBytes(StandardCharsets.US_ASCII));
        return inputStream;
    }

    private InputStream generateSupportedHttpVersionFormat() {
        String rawData = "GET / HTTP/1.2\r\n" +
                "Host: localhost:8080\r\n" +
                "\r\n";
        InputStream inputStream = new ByteArrayInputStream(rawData.getBytes(StandardCharsets.US_ASCII));
        return inputStream;
    }


    private InputStream generateSpaceBeforeColonHeaderTestCase() {
        String rawData = "GET / HTTP/1.1\r\n" +
                "Host : localhost:8080\r\n" +
                "\r\n";
        InputStream inputStream = new ByteArrayInputStream(rawData.getBytes(StandardCharsets.US_ASCII));
        return inputStream;
    }

    private InputStream generateSpaceInHeaderNameTestCase() {
        String rawData = "GET / HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Name with space: localhost:8080\r\n" +
                "\r\n";
        InputStream inputStream = new ByteArrayInputStream(rawData.getBytes(StandardCharsets.US_ASCII));
        return inputStream;
    }

    private InputStream generateQuotedHeaderTestCase() {
        String rawData = "GET / HTTP/1.1\r\n" +
                "Host: \"localhost:8080\"\r\n" +
                "\r\n";
        InputStream inputStream = new ByteArrayInputStream(rawData.getBytes(StandardCharsets.US_ASCII));
        return inputStream;
    }

    private InputStream generateQuotedHeaderWithoutClosingQuoteTestCase() {
        String rawData = "GET / HTTP/1.1\r\n" +
                "Host: \"localhost :8080\r\n" +
                "\r\n";
        InputStream inputStream = new ByteArrayInputStream(rawData.getBytes(StandardCharsets.US_ASCII));
        return inputStream;
    }

    private InputStream generateQuotedHeaderWithNewLineTestCase() {
        String rawData = "GET / HTTP/1.1\r\n" +
                "Host: \"localhost \r\n :8080\"\r\n" +
                "\r\n";
        InputStream inputStream = new ByteArrayInputStream(rawData.getBytes(StandardCharsets.US_ASCII));
        return inputStream;
    }

    private InputStream generateWhitespaceInHeaderNameTestCase() {
        String rawData = "GET / HTTP/1.1\r\n" +
                "Ho st: localhost:8080\r\n" +
                "\r\n";
        InputStream inputStream = new ByteArrayInputStream(rawData.getBytes(StandardCharsets.US_ASCII));
        return inputStream;
    }
    private InputStream generateNoContentLengthHeaderWithTransferEncodingTestCase() {
        String rawData = "GET / HTTP/1.1\r\n" +
                "Transfer-Encoding: chunked\r\n" +
                "Host: postman\r\n" +
                "\r\n";
        InputStream inputStream = new ByteArrayInputStream(rawData.getBytes(StandardCharsets.US_ASCII));
        return inputStream;
    }

    private InputStream generateNoContentLengthHeaderandTransferEncodingTestCase() {
        String rawData = "GET / HTTP/1.1\r\n" +
                "Content-Length: 0\r\n" +
                "Transfer-Encoding: chunked\r\n" +
                "Host: postman\r\n" +
                "\r\n";
        InputStream inputStream = new ByteArrayInputStream(rawData.getBytes(StandardCharsets.US_ASCII));
        return inputStream;
    }


    private InputStream generateValidBodyTestCase() {
        String rawData = "GET / HTTP/1.1\r\n" +
                "Content-Length: 14\r\n" +
                "Host: localhost\r\n" +
                "\r\n" +
                "body1\r\n" +
                "body2\r\n";
        InputStream inputStream = new ByteArrayInputStream(rawData.getBytes(StandardCharsets.US_ASCII));
        return inputStream;
    }
    private InputStream generateHeaderWithInvalidCharacter() {
        String rawData = "GET / HTTP/1.1\r\n" +
                "Content-Length: 14\r\n" +
                "Host: localhost\r\n" +
                "x-header:" + (char) 2 + " localhost\r\n" +
                "\r\n" +
                "body1\r\n" +
                "body2\r\n";
        InputStream inputStream = new ByteArrayInputStream(rawData.getBytes(StandardCharsets.US_ASCII));
        return inputStream;
    }
}
