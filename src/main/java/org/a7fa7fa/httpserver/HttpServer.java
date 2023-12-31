package org.a7fa7fa.httpserver;

import org.a7fa7fa.httpserver.controller.StaticSite;
import org.a7fa7fa.httpserver.config.Configuration;
import org.a7fa7fa.httpserver.config.ConfigurationManager;
import org.a7fa7fa.httpserver.controller.DefaultApiEndpoint;
import org.a7fa7fa.httpserver.core.Router;
import org.a7fa7fa.httpserver.core.ServerListenerThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;

public class HttpServer {

    private final static Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);
    private final static ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);

    public static void main(String[] args){
        LOGGER.info("Server starting...");

        String configLocation = "src/main/resources/http.json";

        if (args != null && args.length == 1) {
            configLocation = args[0];
        } else if (Files.exists(Paths.get("http.json"))) {
            configLocation = "http.json";
        }

        LOGGER.info("Config located : " + configLocation);

        ConfigurationManager.getInstance().loadConfigurationFile(configLocation);
        Configuration conf = ConfigurationManager.getInstance().getCurrentConfiguration();

        LOGGER.info("Using port : " + conf.getPort());
        LOGGER.info("Using webroot : " + conf.getWebroot());
        LOGGER.info("Min file size for compressing : " + conf.getGzipMinFileSizeKb());
        LOGGER.info("Log level : " + conf.getLogLevelLiteral());
        LOGGER.info("Api path : " + conf.getApiPath());

        rootLogger.setLevel(conf.getLoglevel());

        try {
            final Router routes = Router.getInstance(conf);
            routes.register(StaticSite.class);
            routes.register(DefaultApiEndpoint.class);

            final ServerListenerThread serverListenerThread = new ServerListenerThread(conf);
            serverListenerThread.start();
        } catch (Exception e) {
            e.printStackTrace();
            // TODO handel later
        }
    }
}
