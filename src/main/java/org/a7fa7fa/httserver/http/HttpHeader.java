package org.a7fa7fa.httserver.http;

public class HttpHeader {
    private String originalFieldName;
    private String originalFieldValue;

    HttpHeader() {
    }

    public void setFieldName(String nameLiteral) {
        this.originalFieldName = nameLiteral.trim();
    }
    public void setFieldValue(String valueLiteral) {
        this.originalFieldValue = valueLiteral.trim();
    }

    public String getOriginalFieldName() {
        return originalFieldName;
    }

    public String getOriginalFieldValue() {
        return originalFieldValue;
    }

    public String toString(){
        return this.originalFieldName + this.originalFieldValue;
    }
}
