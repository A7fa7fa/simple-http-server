package org.a7fa7fa.httpserver;

import org.a7fa7fa.httpserver.controller.Controller;
import org.a7fa7fa.httpserver.controller.StaticSite;
import org.a7fa7fa.httpserver.config.Configuration;
import org.a7fa7fa.httpserver.config.ConfigurationManager;
import org.a7fa7fa.httpserver.controller.DefaultApiEndpoint;
import org.a7fa7fa.httpserver.core.Router;
import org.a7fa7fa.httpserver.core.ServerListenerThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HttpServer {

    private final static Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);
    private final static ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);

    private Configuration conf;

    public HttpServer() {
        String confPath = "src/main/resources/http.json";

        if (Files.exists(Paths.get("http.json"))) {
            confPath = "http.json";
        }
        this.setConfiguration(confPath);
        LOGGER.info("Config located : " + confPath);
    }

    public HttpServer(String configLocation) {
        this.setConfiguration(configLocation);
        LOGGER.info("Config located : " + configLocation);
    }

    private void setConfiguration(String configLocation) {
        ConfigurationManager.getInstance().loadConfigurationFile(configLocation);
        this.conf = ConfigurationManager.getInstance().getCurrentConfiguration();
        LOGGER.info("Using port : " + this.conf.getPort());
        LOGGER.info("Using webroot : " + this.conf.getWebroot());
        LOGGER.info("Min file size for compressing : " + this.conf.getGzipMinFileSizeKb());
        LOGGER.info("Log level : " + this.conf.getLogLevelLiteral());
        LOGGER.info("Api path : " + this.conf.getApiPath());

        rootLogger.setLevel(this.conf.getLoglevel());
    }

    public void useAsStaticServer() {
        try {
            Router.getInstance(this.conf).register(StaticSite.class);
        } catch (Exception e) {
            LOGGER.error("Could not enable static server.");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public <ControllerInterface extends Controller> void register(Class<ControllerInterface> clazz){
        try {
            Router.getInstance(this.conf).register(clazz);
        } catch (Exception e) {
            LOGGER.error("Could not register Route.");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void start() {
        LOGGER.info("Server starting...");
        try {
            final ServerListenerThread serverListenerThread = new ServerListenerThread(conf);
            serverListenerThread.start();
        } catch (IOException e) {
            e.printStackTrace();
            // TODO handel later
            throw new RuntimeException(e);
        }
    }
}
