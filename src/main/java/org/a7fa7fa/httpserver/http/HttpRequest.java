package org.a7fa7fa.httpserver.http;

import org.a7fa7fa.httpserver.http.tokens.HeaderName;
import org.a7fa7fa.httpserver.http.tokens.HttpMethod;
import org.a7fa7fa.httpserver.http.tokens.HttpStatusCode;
import org.a7fa7fa.httpserver.http.tokens.HttpVersion;

import java.util.HashMap;

public class HttpRequest extends HttpMessage {

    private HttpMethod method;
    private String requestTarget;
    private String originalHttpVersion; // literal from the request
    private HttpVersion bestCompatibleHttpVersion;
    private final HashMap<String, HttpHeader> httpHeaders = new HashMap<String, HttpHeader>();

    private String body;

    HttpRequest(){}

    public HttpMethod getMethod() {
        return method;
    }

    public String getRequestTarget() {
        return requestTarget;
    }

    void setMethod(String methodName) throws HttpParsingException {
        for ( HttpMethod method: HttpMethod.values()) {
            if (methodName.equals((method.name()))){
                this.method = method;
                // this.method = HttpMethod.valueOf(methodName); // string to enum value
                return;
            }
        }
        throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_501_NOT_IMPLEMENTED);
    }

    void setRequestTarget(String requestTarget) throws HttpParsingException {
        if (requestTarget == null || requestTarget.isEmpty()) {
            throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_500_INTERNAL_SEVER_ERROR);
        }
        this.requestTarget = requestTarget;
    }

    public void setHttpVersion(String originalHttpVersion) throws BadHttpVersionException, HttpParsingException {
        this.originalHttpVersion = originalHttpVersion;
        this.bestCompatibleHttpVersion = HttpVersion.getBestCompatibleVersion(originalHttpVersion);
        if (this.bestCompatibleHttpVersion == null) {
            throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_505_HTTP_VERSION_NOT_SUPPORTED);
        }
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public HttpVersion getBestCompatibleHttpVersion() {
        return bestCompatibleHttpVersion;
    }
    public String getOriginalHttpVersion() {
        return originalHttpVersion;
    }

    public HashMap<String, HttpHeader> getHeaders() {
        return httpHeaders;
    }

    public void addHeader(HttpHeader httpHeader) {
        this.httpHeaders.put(httpHeader.getName(), httpHeader);
    }

    public HttpHeader getHeader(HeaderName field) {
        return this.httpHeaders.get(field.getName());
    }

    public boolean isPersistentConnection() {
        return this.getHeader(HeaderName.CONNECTION) != null &&  this.getHeader(HeaderName.CONNECTION).getValue().equalsIgnoreCase("keep-alive") ;
    }

    public String toString(){
        return method + " " + requestTarget + " " + originalHttpVersion;
    }

    public boolean clientNotUnderstandsType(String contentType) {
        HttpHeader header = this.getHeader(HeaderName.ACCEPT);
        if (header == null) {
            return false;
        }
        return !(header.getValue().contains(contentType) || header.getValue().contains("*/*"));
    }


}
