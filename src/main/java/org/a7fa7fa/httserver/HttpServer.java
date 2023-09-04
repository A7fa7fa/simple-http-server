package org.a7fa7fa.httserver;

import ch.qos.logback.classic.Level;
import org.a7fa7fa.httserver.config.Configuration;
import org.a7fa7fa.httserver.config.ConfigurationManager;
import org.a7fa7fa.httserver.core.ServerListenerThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HttpServer {

    private final static Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);
    private final static ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
    public static void main(String[] args){
        rootLogger.setLevel(Level.DEBUG);

        LOGGER.info("Server starting...");

        String path = "src/main/resources/http.json";
        if (args != null && args.length == 1) {
            path = args[0];
        }

        ConfigurationManager.getInstance().loadConfigurationFile(path);
        Configuration conf = ConfigurationManager.getInstance().getCurrentConfiguration();
        LOGGER.info("Using port: " + conf.getPort());
        LOGGER.info("Using webroot: " + conf.getWebroot());
        LOGGER.info("Min file size for compressing: " + conf.getGzipMinFileSizeKb());

        try {
            final ServerListenerThread serverListenerThread = new ServerListenerThread(conf.getPort(), conf.getWebroot(), conf.getGzipMinFileSizeKb());
            serverListenerThread.start();
        } catch (IOException e) {
            e.printStackTrace();
            // TODO handel later
        }
    }
}
