package com.github.veithen.dfpagent.protocol.agent;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;


public final class Agent implements Runnable {
    private final ServerSocket serverSocket;
    private final List<Peer> peers = new LinkedList<Peer>();
    private final WeightInfoProvider weightInfoProvider;
    private boolean stopping;
    
    public Agent(ServerSocket serverSocket, WeightInfoProvider weightInfoProvider) {
        this.serverSocket = serverSocket;
        this.weightInfoProvider = weightInfoProvider;
    }

    WeightInfoProvider getWeightInfoProvider() {
        return weightInfoProvider;
    }

    public void run() {
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                synchronized (this) {
                    if (stopping) {
                        socket.close();
                        break;
                    }
                    Peer peer = new Peer(this, socket);
                    peers.add(peer);
                    new Thread(peer, "DFP Connection Thread: " + peer.getIdentifier()).start();
                }
            }
        } catch (IOException ex) {
            // TODO
        }
    }
    
    public synchronized void stop() throws IOException {
        stopping = true;
        serverSocket.close();
        for (Peer peer : peers) {
            // TODO
        }
    }
}
