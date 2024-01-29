/* Copyright (c) 2007-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package minesweeper.server;

import org.junit.Test;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Random;

public class MinesweeperServerTest {
    /* Test strategy */
    // Multi-threading
    // - Multiple clients should be able to connect

    private static final String LOCALHOST = "127.0.0.1";
    private static final int PORT = 4000 + new Random().nextInt(1 << 15);
    private static final int MAX_CONNECTION_ATTEMPTS = 10;
    private static final int CLIENT_READ_SOCKET_TIMEOUT = 3000;

    private static final Object multiThreadingTestLock = new Object();
    private static int multiThreadingTestNumConnectedClients = 0;
    private static boolean multiThreadingTestDone = false;

    /* Multi-threading test */
    @Test
    public void testMultiThreading() throws InterruptedException {
        Thread server = startMinesweeperServer();
        Thread client1 = new Thread(new ClientConnector(server)), client2 = new Thread(new ClientConnector(server)),
                client3 = new Thread(new ClientConnector(server));
        client1.start();
        client2.start();
        client3.start();
        multiThreadingTestDone = true;
        client1.join();
        client2.join();
        client3.join();
    }

    /**
     * Start a MinesweeperServer in debug mode.
     *
     * @return thread running the server
     */
    private static Thread startMinesweeperServer() {
        final String[] args = new String[]{"--debug", "--port", Integer.toString(PORT)};
        Thread serverThread = new Thread(() -> MinesweeperServer.main(args));
        serverThread.start();
        return serverThread;
    }

    /**
     * Connect to a MinesweeperServer.
     *
     * @param server the server thread
     * @return socket connected to the server
     * @throws java.io.IOException if the connection fails
     */
    private static Socket connectToMinesweeperServer(Thread server) throws IOException {
        int attempts = 0;
        while (true) {
            try {
                Socket socket = new Socket(LOCALHOST, PORT);
                socket.setSoTimeout(CLIENT_READ_SOCKET_TIMEOUT);
                return socket;
            } catch (ConnectException ce) {
                if (!server.isAlive()) {
                    throw new IOException("Server thread not running");
                }
                if (++attempts > MAX_CONNECTION_ATTEMPTS) {
                    throw new IOException("Exceeded max connection attempts", ce);
                }
                try { Thread.sleep(attempts * 10); } catch (InterruptedException ie) { }
            }
        }
    }

    private static class ClientConnector implements Runnable {
        private static final int BUSY_WAIT_TIME = 1000;
        private final Thread server;

        public ClientConnector(Thread server) {
            this.server = server;
        }

        @Override
        public void run() {
            Socket client = null;
            try {
                client = connectToMinesweeperServer(server);
                synchronized (multiThreadingTestLock) {
                    ++multiThreadingTestNumConnectedClients;
                }
                while (true) {
                    synchronized (multiThreadingTestLock) {
                        if (multiThreadingTestNumConnectedClients > 1 && multiThreadingTestDone) {
                            return;
                        }
                    }
                    Thread.sleep(BUSY_WAIT_TIME);
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    if (client != null) {
                        client.close();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
