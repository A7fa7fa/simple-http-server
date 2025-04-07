package org.a7fa7fa.myserver;

import org.a7fa7fa.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;


public class MyServer {
    private final static Logger LOGGER = LoggerFactory.getLogger(org.a7fa7fa.httpserver.HttpServer.class);

    public static void main(String[] args){
        String configLocation = "";

        if (args != null && args.length == 1) {
            configLocation = args[0];
        } else if (Files.exists(Paths.get("http.json"))) {
            configLocation = "http.json";
        } else {
            throw new RuntimeException("Missing config file.");
        }

        HttpServer server = new HttpServer(configLocation);
        server.useAsStaticServer();
        server.register(DefaultApiEndpoint.class);
        server.start();
    }
}
