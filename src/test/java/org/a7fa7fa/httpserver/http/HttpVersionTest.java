package org.a7fa7fa.httpserver.http;

import org.a7fa7fa.httpserver.http.tokens.HttpVersion;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HttpVersionTest {

    @Test
    void getBestCompatibleVersionExactMatch(){
        HttpVersion version = null;
        try {
            version = HttpVersion.getBestCompatibleVersion("HTTP/1.1");
            assertNotNull(version);
            assertEquals(version, HttpVersion.HTTP_1_1);
        } catch (BadHttpVersionException e) {
            fail();
        }
    }
    @Test
    void getBestCompatibleVersionBadFormat(){
        HttpVersion version = null;
        try {
            version = HttpVersion.getBestCompatibleVersion("http/1.1");
            fail();
        } catch (BadHttpVersionException e) {
        }
    }

    @Test
    void getBestCompatibleVersionHigherMinorVersion(){
        HttpVersion version = null;
        try {
            version = HttpVersion.getBestCompatibleVersion("HTTP/1.2");
            assertNotNull(version);
            assertEquals(version, HttpVersion.HTTP_1_1);
        } catch (BadHttpVersionException e) {
            fail();
        }
    }

}