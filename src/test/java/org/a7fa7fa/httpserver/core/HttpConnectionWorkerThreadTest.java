package org.a7fa7fa.httpserver.core;

import org.a7fa7fa.httpserver.config.Configuration;
import org.a7fa7fa.httpserver.parser.ByteProcessor;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HttpConnectionWorkerThreadTest {

    private Configuration config;

    @BeforeAll
    public void beforeClass() throws Exception {
        config = new Configuration();
        config.setApiPath("api");
        config.setPort(8080);
        config.setLogLevel("error");
        config.setGzipMinFileSizeKb(5);
        config.setHost("localhost");
        Router router = Router.getInstance(config);
        router.register(MockController.class);
    }

    @Test
    void runValidRequest()  {
        try {
            MockSocket mockSocket = new MockSocket();
            mockSocket.setInput(this.validInputCase());


            HttpConnectionWorkerThread workerThread = new HttpConnectionWorkerThread(mockSocket, config);
            assertNotNull(workerThread);

            workerThread.run();

            byte[] writtenOutput =  mockSocket.getBytesList();

            String out = ByteProcessor.byteToString(writtenOutput);

            String expected = "HTTP/1.1 200 OK\r\n" +
                    "date: 12345\r\n" +
                    "content-length: 25\r\n" +
                    "server: simple-http-server\r\n" +
                    "host: localhost\r\n" +
                    "connection: close\r\n" +
                    "content-type: text\r\n" +
                    "\r\n" +
                    "this is the file returned";

            assertEquals(expected, out);
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void runMandatoryHostHeader() {
        try {
            MockSocket mockSocket = new MockSocket();
            mockSocket.setInput("GET / HTTP/1.1\r\n" +
                    "Connection: keep-alive\r\n" +
                    "\r\n");


            HttpConnectionWorkerThread workerThread = new HttpConnectionWorkerThread(mockSocket, config);
            assertNotNull(workerThread);

            workerThread.run();

            byte[] writtenOutput =  mockSocket.getBytesList();

            String out = ByteProcessor.byteToString(writtenOutput);

            assertTrue(out.startsWith("HTTP/1.1 400 Bad Request"));
            assertTrue(out.contains("server: simple-http-server"));
            assertTrue(out.contains("host: localhost"));
        } catch (Exception e) {
            fail(e);
        }
    }


    @Test
    void runTargetNotFound() {
        try {
            MockSocket mockSocket = new MockSocket();
            mockSocket.setInput(this.notFound());


            HttpConnectionWorkerThread workerThread = new HttpConnectionWorkerThread(mockSocket, config);
            assertNotNull(workerThread);

            workerThread.run();

            byte[] writtenOutput =  mockSocket.getBytesList();

            String out = ByteProcessor.byteToString(writtenOutput);

            assertTrue(out.startsWith("HTTP/1.1 404 Not found"));
            assertTrue(out.contains("server: simple-http-server"));
            assertTrue(out.contains("host: localhost"));
    } catch (Exception e) {
        fail(e);
        }
    }

    private String validInputCase() {
        return "GET / HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: close\r\n" +
                "Upgrade-Insecure-Requests: 1\r\n" +
                "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36\r\n" +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\r\n" +
                "Sec-Fetch-Site: none\r\n" +
                "Sec-Fetch-Mode: navigate\r\n" +
                "Accept-Encoding: gzip, deflate, br\r\n" +
                "Accept-Language: en-US,en;q=0.9\r\n" +
                "\r\n";
    }
    private String notFound() {
        return "GET /api/something HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "Upgrade-Insecure-Requests: 1\r\n" +
                "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36\r\n" +
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7\r\n" +
                "Sec-Fetch-Site: none\r\n" +
                "Sec-Fetch-Mode: navigate\r\n" +
                "Accept-Encoding: gzip, deflate, br\r\n" +
                "Accept-Language: en-US,en;q=0.9\r\n" +
                "\r\n";
    }


}