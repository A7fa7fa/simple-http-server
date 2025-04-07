package org.a7fa7fa.httpserver.config;

import ch.qos.logback.classic.Level;

public class Configuration {

    private int port;
    private String webroot;
    private int gzipMinFileSizeKb;
    private String logLevel;
    private String apiPath;
    private int maxBodySize;
    private String fileLocation;

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
        String path = apiPath;
        if (apiPath != null && !apiPath.isEmpty() && !apiPath.startsWith("/") ) {
            path = "/"+apiPath;
        }
        this.apiPath = path;
    }

    public int getMaxBodySize() {
        return maxBodySize;
    }

    public void setMaxBodySize(int maxBodySize) {
        this.maxBodySize = maxBodySize;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }
}
