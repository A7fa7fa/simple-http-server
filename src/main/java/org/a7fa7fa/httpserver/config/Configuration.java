package org.a7fa7fa.httpserver.config;

import ch.qos.logback.classic.Level;

public class Configuration {
    private int port;
    private String webroot;
    private int gzipMinFileSizeKb;
    private String logLevel;
    private String apiPath;

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
    public String getLogLevelLiteral() {
        return logLevel;
    }
    public Level getLoglevel() {
        return LogLevel.parseLogLevel(logLevel);
    }
    public void setLogLevel(String logLevel) {this.logLevel = logLevel; }

    public String getApiPath() {
        return apiPath;
    }

    public void setApiPath(String apiPath) {
        this.apiPath = apiPath;
    }
}
