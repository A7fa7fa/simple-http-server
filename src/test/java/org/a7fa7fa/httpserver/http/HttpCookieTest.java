package org.a7fa7fa.httpserver.http;

import org.a7fa7fa.httpserver.http.tokens.HeaderName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HttpCookieTest {

    @Test
    void extractValueFirst() {
        String value = "session=asdasdasda; asdasd=asds;";
        String res = HttpCookie.extractValue("session", new HttpHeader(HeaderName.COOKIE, value));
        assertEquals(res, "asdasdasda");

    }

    @Test
    void extractValueSecond() {
        String value = "session=asdasdasda; asdasd=asds;";
        String res = HttpCookie.extractValue("asdasd", new HttpHeader(HeaderName.COOKIE, value));
        assertEquals(res, "asds");

    }

    @Test
    void extractValueMissing() {
        String value = "session=asdasdasda; asdasd=asds;";
        String res = HttpCookie.extractValue("qwert", new HttpHeader(HeaderName.COOKIE, value));
        assertEquals(res, null);

    }

    @Test
    void extractValueEmpty() {
        String value = "session=;";
        String res = HttpCookie.extractValue("session", new HttpHeader(HeaderName.COOKIE, value));
        assertEquals(res, "");
    }

    @Test
    void extractValueCorrupt() {
        String value = "session;d;";
        String res = HttpCookie.extractValue("session", new HttpHeader(HeaderName.COOKIE, value));
        assertEquals(res, null);
    }

    @Test
    void extractValueCorrupt2() {
        String value = ";d;";
        String res = HttpCookie.extractValue("session", new HttpHeader(HeaderName.COOKIE, value));
        assertEquals(res, null);
    }
}
