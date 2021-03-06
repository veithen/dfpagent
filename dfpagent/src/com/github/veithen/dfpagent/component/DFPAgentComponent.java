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
package com.github.veithen.dfpagent.component;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.github.veithen.dfpagent.Constants;
import com.github.veithen.dfpagent.protocol.Protocol;
import com.github.veithen.dfpagent.protocol.agent.Agent;
import com.github.veithen.dfpagent.protocol.agent.WeightInfo;
import com.github.veithen.dfpagent.protocol.agent.WeightInfoProvider;
import com.github.veithen.dfpagent.resources.Messages;
import com.ibm.ejs.ras.Tr;
import com.ibm.ejs.ras.TraceComponent;
import com.ibm.websphere.models.config.ipc.EndPoint;
import com.ibm.ws.exception.RuntimeError;
import com.ibm.ws.exception.RuntimeWarning;
import com.ibm.ws.runtime.service.EndPointMgr;
import com.ibm.ws.runtime.service.EndPointMgr.ServerEndPoints;
import com.ibm.ws.runtime.service.Server;
import com.ibm.wsspi.runtime.component.WsComponentImpl;
import com.ibm.wsspi.runtime.service.WsServiceRegistry;

public class DFPAgentComponent extends WsComponentImpl implements PropertyChangeListener, WeightInfoProvider {
    private static final TraceComponent TC = Tr.register(DFPAgentComponent.class, Constants.TRACE_GROUP, Messages.class.getName());
    
    private Agent agent;
    private int httpPort;
    private Inet4Address[] httpAddresses;
    
    private final Object weightLock = new Object();
    private int weight;
    
    @Override
    public void start() throws RuntimeError, RuntimeWarning {
        EndPointMgr epMgr;
        Server server;
        try {
            epMgr = WsServiceRegistry.getService(this, EndPointMgr.class);
            server = WsServiceRegistry.getService(this, Server.class);
        } catch (Exception ex) {
            throw new RuntimeError(ex);
        }
        ServerEndPoints endpoints = epMgr.getNodeEndPoints("@").getServerEndPoints("@");
        
        // Determine the TCP port to bind the DFP agent to
        EndPoint agentEndpoint = endpoints.getEndPoint("DFP_AGENT_ADDRESS");
        if (agentEndpoint == null) {
            Tr.info(TC, Messages._0001I);
            return;
        }
        int agentPort = agentEndpoint.getPort();
        
        // Determine the port and addresses for the WC_defaulthost endpoint
        EndPoint httpEndpoint = endpoints.getEndPoint("WC_defaulthost");
        httpPort = httpEndpoint.getPort();
        String httpHost = httpEndpoint.getHost();
        List<Inet4Address> httpAddresses = new ArrayList<Inet4Address>();
        try {
            if (httpHost.equals("*")) {
                for (Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements(); ) {
                    for (Enumeration<InetAddress> addresses = ifaces.nextElement().getInetAddresses(); addresses.hasMoreElements(); ) {
                        InetAddress addr = addresses.nextElement();
                        if (!addr.isLoopbackAddress() && addr instanceof Inet4Address) {
                            httpAddresses.add((Inet4Address)addr);
                        }
                    }
                }
            } else {
                InetAddress addr = InetAddress.getByName(httpHost);
                if (addr instanceof Inet4Address) {
                    httpAddresses.add((Inet4Address)addr);
                }
            }
        } catch (IOException ex) {
            throw new RuntimeError(ex);
        }
        if (httpAddresses.isEmpty()) {
            Tr.info(TC, Messages._0016I);
            return;
        }
        this.httpAddresses = httpAddresses.toArray(new Inet4Address[httpAddresses.size()]);
        if (TC.isDebugEnabled()) {
            Tr.debug(TC, "HTTP port: {0}; addresses: {1}", new Object[] { httpPort, httpAddresses });
        }
        
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(agentPort);
        } catch (IOException ex) {
            // TODO: test the behavior if port is already in use
            throw new RuntimeError(ex);
        }
        agent = new Agent(serverSocket, this);
        new Thread(agent, "DFP Acceptor Thread").start();
        
        // We register the listener on the Server component (not the ApplicationServer component)
        // to ensure that we get notified after the HTTP port has been opened and before the HTTP
        // port is closed. With the ApplicationServer component, that is not the case.
        server.addPropertyChangeListener("state", this);
        
        Tr.info(TC, Messages._0002I, agentPort);
    }

    public void propertyChange(PropertyChangeEvent event) {
        if (TC.isDebugEnabled()) {
            Tr.debug(TC, "Got a state change event from ApplicationServer. Old state: {0}; new state: {1}",
                    new Object[] { event.getOldValue(), event.getNewValue() });
        }
        String value = (String)event.getNewValue();
        if (value.equals("STARTED")) {
            updateWeight(1);
        } else if (value.equals("STOPPING")) {
            updateWeight(0);
        }
    }

    private void updateWeight(int weight) {
        synchronized (weightLock) {
            if (TC.isInfoEnabled()) {
                Tr.info(TC, Messages._0017I, new Object[] { this.weight, weight });
            }
            this.weight = weight;
        }
        agent.sendPreferenceInformation();
    }
    
    public WeightInfo[] getWeightInfo() {
        int weight;
        synchronized (weightLock) {
            weight = this.weight;
        }
        WeightInfo[] result = new WeightInfo[httpAddresses.length];
        for (int i=0; i<httpAddresses.length; i++) {
            result[i] = new WeightInfo(httpAddresses[i], Protocol.TCP, httpPort, weight);
        }
        return result;
    }

    @Override
    public void stop() {
        if (agent != null) {
            try {
                agent.stop();
            } catch (IOException ex) {
                Tr.error(TC, Messages._0004E, ex);
            }
            agent = null;
            Tr.info(TC, Messages._0003I);
        }
    }
}
