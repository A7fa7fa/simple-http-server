package org.a7fa7fa.httpserver.config;

public class Configuration {
    private int port;
    private String webroot;
    private int gzipMinFileSizeKb;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getWebroot() {
        return webroot;
    }

    public void setWebroot(String webroot) {
        this.webroot = webroot;
    }

    public int getGzipMinFileSizeKb() {
        return gzipMinFileSizeKb;
    }

    public void setGzipMinFileSizeKb(int gzipMinFileSizeKb) {
        this.gzipMinFileSizeKb = gzipMinFileSizeKb;
    }
}
