package org.a7fa7fa.httpserver.http;

public enum HeaderName {
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    CONTENT_ENCODING("Content-Encoding"),
    SERVER("Server"),
    ACCEPT_ENCODING("Accept-Encoding"),
    TRANSFER_ENCODING("Transfer-Encoding"),
    ACCEPT("Accept");

    private final String fieldNameLowerCase;

    HeaderName(String name){
        this.fieldNameLowerCase = name.toLowerCase();
    }

    public String getName(){
        return this.fieldNameLowerCase;
    }

    public static HeaderName findHeaderField(String fieldName) {
        final int COLON = 0x3A; // 58
        StringBuilder name = new StringBuilder(fieldName.trim().toLowerCase());
        if (name.charAt(name.length() - 1) == (char) COLON) {
            name.deleteCharAt(name.length() - 1);
        }
        for (HeaderName field : HeaderName.values()) {
            if (field.getName().contentEquals(name)) {
                return field;
            }
        }
        return null;
    }
}
