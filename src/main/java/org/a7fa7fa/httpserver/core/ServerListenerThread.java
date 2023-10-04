package org.a7fa7fa.httpserver.core;

import org.a7fa7fa.httpserver.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerListenerThread extends Thread {

    private final static Logger LOGGER = LoggerFactory.getLogger(ServerListenerThread.class);
    private final ServerSocket serverSocket;
    private final Configuration configuration;

    public ServerListenerThread(Configuration configuration) throws IOException {
        this.configuration = configuration;
        this.serverSocket = new ServerSocket(configuration.getPort());
    }

    @Override
    public void run() {
        LOGGER.info("Ready to accept connections...");
        try {
            while (serverSocket.isBound() && !serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                LOGGER.info("Connection accepted: " + socket.getInetAddress());
                HttpConnectionWorkerThread workerThread = new HttpConnectionWorkerThread(socket, this.configuration);
                workerThread.start();
            }
        } catch (IOException e) {
            LOGGER.error("Problem with setting socket", e);
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {}
            }
            LOGGER.info("Server socket closed.");
        }
    }
}
