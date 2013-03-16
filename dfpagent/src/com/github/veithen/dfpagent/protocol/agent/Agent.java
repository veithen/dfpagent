package com.github.veithen.dfpagent.protocol.agent;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import com.github.veithen.dfpagent.Constants;
import com.github.veithen.dfpagent.resources.Messages;
import com.ibm.ejs.ras.Tr;
import com.ibm.ejs.ras.TraceComponent;

public final class Agent implements Runnable {
    private static final TraceComponent TC = Tr.register(Agent.class, Constants.TRACE_GROUP, Messages.class.getName());
    
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
        boolean stopping = false;
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                synchronized (this) {
                    stopping = this.stopping;
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
            if (!stopping) {
                Tr.error(TC, Messages._0013E, ex);
            }
        }
    }
    
    public synchronized void stop() throws IOException {
        stopping = true;
        serverSocket.close();
        for (Peer peer : peers) {
            peer.stop();
        }
    }
}
