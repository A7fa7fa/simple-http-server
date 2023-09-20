package org.a7fa7fa.httpserver.http;

import org.a7fa7fa.httpserver.http.tokens.HeaderName;

import java.nio.charset.StandardCharsets;

public class HttpHeader {

    private static final String DELIMITER = ":";

    private static final String SP = " ";
    private HeaderName field;
    private String originalFieldName;
    private String originalFieldValue;

    HttpHeader() {
    }
    public HttpHeader(HeaderName headerFiled, String fieldValue) {
        this.setName(headerFiled);
        this.setValue(fieldValue);
    }

    public void setName(HeaderName headerField) {
        this.originalFieldName = headerField.getName();
        this.field = headerField;
    }
    public void setName(String nameLiteral) {
        if (nameLiteral.endsWith(DELIMITER)) {
            nameLiteral = nameLiteral.substring(0,  nameLiteral.length()-1);
        }
        nameLiteral = nameLiteral.toLowerCase();
        this.originalFieldName = nameLiteral;
        this.field = HeaderName.findHeaderField(nameLiteral);
    }

    public void setValue(String valueLiteral) {
        this.originalFieldValue = valueLiteral.trim();
    }

    private String getOriginalFieldName() {
        return originalFieldName;
    }

    private String getOriginalFieldValue() {
        return this.originalFieldValue;
    }

    public String getValue() {
        return this.getOriginalFieldValue();
    }
    public String getName() {
        if (field == null){
            return getOriginalFieldName();
        }
        return field.getName();

    }

    public HeaderName getHeaderField() {
        return field;
    }

    public String toString(){
        return this.toStandardFormat();
    }

    public String toStandardFormat() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getName());
        sb.append(DELIMITER);
        sb.append(SP);
        sb.append(this.getValue());
        return sb.toString();
    }
    public byte[] getBytes() {
        return this.toStandardFormat().getBytes(StandardCharsets.US_ASCII);
    }
}
