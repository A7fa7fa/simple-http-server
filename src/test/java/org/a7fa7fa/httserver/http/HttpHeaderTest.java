package org.a7fa7fa.httserver.http;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HttpHeaderTest {
    @Test
    void setNameWithField() {
        HttpHeader header = new HttpHeader();
        header.setName(HeaderName.CONTENT_LENGTH);
        assertNotNull(header);
        assertEquals(header.getName(), HeaderName.CONTENT_LENGTH.getName());
        assertEquals(header.getHeaderField(), HeaderName.CONTENT_LENGTH);
    }
    @Test
    void setNameWithFieldString() {
        HttpHeader header = new HttpHeader();
        header.setName(HeaderName.CONTENT_LENGTH.getName());
        assertNotNull(header);
        assertEquals(header.getName(), HeaderName.CONTENT_LENGTH.getName());
        assertEquals(header.getHeaderField(), HeaderName.CONTENT_LENGTH);
    }
    @Test
    void setNameWithString() {
        HttpHeader header = new HttpHeader();
        header.setName("test-name");
        assertNotNull(header);
        assertEquals(header.getName(), "test-name");
        assertNull(header.getHeaderField());
    }
    @Test
    void setNameRemoveColon() {
        HttpHeader header = new HttpHeader();
        header.setName("test-name:");
        assertNotNull(header);
        assertEquals(header.getName(), "test-name");
        assertNull(header.getHeaderField());
    }

    @Test
    void setNameToLowerCase() {
        HttpHeader header = new HttpHeader();
        header.setName("Test-Name:");
        assertNotNull(header);
        assertEquals(header.getName(), "test-name");
        assertNull(header.getHeaderField());
    }

    @Test
    void getNameOriginalValue() {
        HttpHeader header = new HttpHeader();
        header.setName("Test-Name");
        assertNotNull(header);
        assertEquals(header.getName(), "test-name");
    }
    @Test
    void getNameKnownHeaderName() {
        HttpHeader header = new HttpHeader();
        header.setName(HeaderName.ACCEPT);
        assertNotNull(header);
        assertEquals(header.getName(), HeaderName.ACCEPT.getName());
    }

    @Test
    void toStandardFormat() {
        HttpHeader header = new HttpHeader(HeaderName.ACCEPT, "*/*");
        assertNotNull(header);
        assertEquals(header.getName(), HeaderName.ACCEPT.getName());
        assertEquals(header.getValue(), "*/*");
    }

}