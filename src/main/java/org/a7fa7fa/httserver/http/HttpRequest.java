package org.a7fa7fa.httserver.http;

import java.util.ArrayList;

public class HttpRequest extends HttpMessage {

    private HttpMethod method;
    private String requestTarget;
    private String originalHttpVersion; // literal from the request
    private HttpVersion bestCompatibleHttpVersion;
    private final ArrayList<HttpHeader> httpHeaders = new ArrayList<HttpHeader>();

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

    public HttpVersion getBestCompatibleHttpVersion() {
        return bestCompatibleHttpVersion;
    }
    public String getOriginalHttpVersion() {
        return originalHttpVersion;
    }

    public ArrayList<HttpHeader> getHeaders() {
        return httpHeaders;
    }

    public void addHeader(HttpHeader httpHeader) {
        this.httpHeaders.add(httpHeader);
    }

    public String toString(){
        return method + " " + requestTarget + " " + originalHttpVersion;
    }

}
