package org.example;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Http11Server {

    private final int listenPort;

    private final FileResolver fileResolver;

    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    public Http11Server(int listenPort, String documentRoot) {
        this.listenPort = listenPort;
        this.fileResolver = new DocumentRootFileResolver(documentRoot);
    }

    public void start() throws IOException {
        try (ServerSocket socket = new ServerSocket()) {
            socket.bind(new InetSocketAddress("0.0.0.0", listenPort));

            while (true) {
                final Socket accepted = socket.accept();
                threadPool.submit(() -> {
                    try {
                        new Http11Processor(accepted.getInputStream(), accepted.getOutputStream(),
                                fileResolver).process();
                    } catch (IOException e) {
                        // TCP wire error throws IOException
                        // TODO error handling
                    } finally {
                        closeQuietly(accepted);
                    }
                });
            }
        }
    }

    private void closeQuietly(Socket socket) {
        try {
            socket.close();
        } catch (IOException ignoreable) {
        }
    }
}
