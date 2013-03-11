package com.github.veithen.dfpagent;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class Agent implements Runnable {
    private final ServerSocket serverSocket;
    private final List<Connection> connections = new LinkedList<Connection>();
    private final int httpPort;
    private boolean stopping;
    
    public Agent(ServerSocket serverSocket, int httpPort) {
        this.serverSocket = serverSocket;
        this.httpPort = httpPort;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                synchronized (this) {
                    if (stopping) {
                        socket.close();
                        break;
                    }
                    Connection connection = new Connection(socket);
                    connections.add(connection);
                    new Thread(connection, "DFP Connection Thread: " + connection.getPeerIdentifier()).start();
                }
            }
        } catch (IOException ex) {
            // TODO
        }
    }
    
    public void updateWeight(int weight) {
        
    }
    
    public synchronized void stop() throws IOException {
        stopping = true;
        serverSocket.close();
        for (Connection connection : connections) {
            // TODO
        }
    }
}
