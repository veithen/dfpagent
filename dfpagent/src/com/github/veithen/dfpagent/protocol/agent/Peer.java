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
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.veithen.dfpagent.Constants;
import com.github.veithen.dfpagent.protocol.connection.Connection;
import com.github.veithen.dfpagent.protocol.connection.Handler;
import com.github.veithen.dfpagent.protocol.connection.LogInterceptor;
import com.github.veithen.dfpagent.protocol.message.Message;
import com.github.veithen.dfpagent.protocol.message.MessageType;
import com.github.veithen.dfpagent.protocol.message.MissingTLVException;
import com.github.veithen.dfpagent.protocol.tlv.TLV;
import com.github.veithen.dfpagent.protocol.tlv.Type;
import com.github.veithen.dfpagent.protocol.tlv.ValueWriter;
import com.github.veithen.dfpagent.resources.Messages;
import com.ibm.ejs.ras.Tr;
import com.ibm.ejs.ras.TraceComponent;

/**
 * Represents a DFP Manager or SLB connecting to an {@link Agent}.
 */
final class Peer implements Runnable, Handler {
    private static final TraceComponent TC = Tr.register(Peer.class, Constants.TRACE_GROUP, Messages.class.getName());
    
    private final Agent agent;
    private final Connection connection;
    private final KeepAliveManager keepAliveManager;
    private final String identifier;
    
    Peer(Agent agent, Socket socket) throws IOException {
        this.agent = agent;
        identifier = socket.getRemoteSocketAddress().toString();
        connection = new Connection(socket, this);
        connection.addInterceptor(new LogInterceptor(identifier));
        keepAliveManager = new KeepAliveManager(this);
    }

    /**
     * Get the identifier of the peer. This identifier is meant to be used in log messages.
     * 
     * @return the peer identifier
     */
    String getIdentifier() {
        return identifier;
    }
    
    public void run() {
        Tr.info(TC, Messages._0014I, identifier);
        new Thread(keepAliveManager, "DFP Keep-Alive Thread: " + identifier).start();
        try {
            connection.run();
        } finally {
            keepAliveManager.stop();
            Tr.info(TC, Messages._0015I, identifier);
        }
    }
    
    void stop() {
        connection.stop();
    }

    public void processMessage(Message message) throws IOException {
        try {
            switch (message.getType()) {
                case DFP_PARAMETERS:
                    TLV keepAliveTLV = message.getRequiredTLV(Type.KEEP_ALIVE);
                    if (keepAliveTLV.getDataLength() != 4) {
                        Tr.error(TC, Messages._0011E, message.getType().getName());
                        return;
                    }
                    int keepAlive = keepAliveTLV.getValue().readInt();
                    if (TC.isDebugEnabled()) {
                        Tr.debug(TC, "Got new keep-alive value from peer {0}: {1}", new Object[] { identifier, keepAlive });
                    }
                    keepAliveManager.setKeepAlive(keepAlive);
                    break;
                default:
                    Tr.warning(TC, Messages._0007W, message.getType().getName());
            }
        } catch (MissingTLVException ex) {
            Tr.error(TC, Messages._0008E, new Object[] { ex.getMessageType().getName(), ex.getType().getName() });
        }
    }

    /**
     * Send a Preference Information message as described in section 6.1 of the DFP specification.
     * This method will call {@link WeightInfoProvider#getWeightInfo()} to get the current weights.
     */
    void sendPreferenceInformation() throws IOException {
        // From the DFP spec: "The real servers are first grouped by
        // their port number and protocol type requiring a separate Load TLV for
        // each grouping."
        Map<PortAndProtocol,List<WeightInfo>> loadMap = new HashMap<PortAndProtocol,List<WeightInfo>>();
        for (WeightInfo weightInfo : agent.getWeightInfoProvider().getWeightInfo()) {
            PortAndProtocol key = new PortAndProtocol(weightInfo.getPort(), weightInfo.getProtocol());
            List<WeightInfo> list = loadMap.get(key);
            if (list == null) {
                list = new ArrayList<WeightInfo>();
                loadMap.put(key, list);
            }
            list.add(weightInfo);
        }
        List<TLV> tlvs = new ArrayList<TLV>(loadMap.size());
        for (Map.Entry<PortAndProtocol,List<WeightInfo>> entry : loadMap.entrySet()) {
            TLV load = new TLV(Type.LOAD);
            ValueWriter out = load.getValueWriter();
            // ** Port Number **
            out.writeShort(entry.getKey().getPort());
            // ** Protocol **
            out.writeByte(entry.getKey().getProtocol().getCode());
            // ** Flags **
            out.writeByte(0);
            // ** Number of Hosts **
            out.writeShort(entry.getValue().size());
            // ** Reserved **
            out.writeShort(0);
            for (WeightInfo weightInfo : entry.getValue()) {
                // ** IP Address **
                out.write(weightInfo.getAddress().getAddress());
                // ** BindID **
                out.writeShort(0);
                // ** Weight **
                out.writeShort(weightInfo.getWeight());
            }
            out.commit();
            tlvs.add(load);
        }
        connection.sendMessage(new Message(MessageType.PREFERENCE_INFORMATION, tlvs));
    }
}
