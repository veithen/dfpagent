/*
 * DFP Agent for WebSphere
 * Copyright (C) 2013 Andreas Veithen
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License, version 2, as
 * published by the Free Software Foundation, and/or the GNU Lesser
 * General Public License, version 2.1, also as published by the Free
 * Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License and the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License
 * and the GNU Lesser General Public License along with this program;
 * if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
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
    
    /**
     * Send a Preference Information message to all peers to inform them of updated server weights.
     */
    public void sendPreferenceInformation() {
        for (Peer peer : peers) {
            try {
                peer.sendPreferenceInformation();
            } catch (IOException ex) {
                Tr.error(TC, Messages._0018E, new Object[] { peer.getIdentifier(), ex });
            }
        }
    }
}
