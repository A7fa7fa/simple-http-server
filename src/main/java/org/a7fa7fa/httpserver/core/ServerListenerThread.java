package org.a7fa7fa.httpserver.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerListenerThread extends Thread {
    private final static Logger LOGGER = LoggerFactory.getLogger(ServerListenerThread.class);

    private final String webroot;
    private final ServerSocket serverSocket;
    private final int gzipMinFileSizeKb;

    public ServerListenerThread(int port, String webroot, int gzipMinFileSizeKb) throws IOException {
        this.webroot = webroot;
        this.serverSocket = new ServerSocket(port);
        this.gzipMinFileSizeKb = gzipMinFileSizeKb;
    }

    @Override
    public void run() {
        try {

            while (serverSocket.isBound() && !serverSocket.isClosed()) {

                Socket socket = serverSocket.accept();
                LOGGER.info("Connection accepted: " + socket.getInetAddress());
                HttpConnectionWorkerThread workerThread = new HttpConnectionWorkerThread(socket, this.webroot, this.gzipMinFileSizeKb);
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
