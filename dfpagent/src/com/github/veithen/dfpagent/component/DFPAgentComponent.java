package com.github.veithen.dfpagent.component;

import java.io.IOException;
import java.net.ServerSocket;

import com.github.veithen.dfpagent.Agent;
import com.github.veithen.dfpagent.Constants;
import com.github.veithen.dfpagent.resources.Messages;
import com.ibm.ejs.ras.Tr;
import com.ibm.ejs.ras.TraceComponent;
import com.ibm.websphere.models.config.ipc.EndPoint;
import com.ibm.ws.exception.RuntimeError;
import com.ibm.ws.exception.RuntimeWarning;
import com.ibm.ws.runtime.service.EndPointMgr;
import com.ibm.ws.runtime.service.EndPointMgr.ServerEndPoints;
import com.ibm.wsspi.runtime.component.WsComponentImpl;
import com.ibm.wsspi.runtime.service.WsServiceRegistry;

public class DFPAgentComponent extends WsComponentImpl {
    private static final TraceComponent TC = Tr.register(DFPAgentComponent.class, Constants.TRACE_GROUP, Messages.class.getName());
    
    private Agent agent;
    
    @Override
    public void start() throws RuntimeError, RuntimeWarning {
        EndPointMgr epMgr;
        try {
            epMgr = WsServiceRegistry.getService(this, EndPointMgr.class);
        } catch (Exception ex) {
            throw new RuntimeError(ex);
        }
        ServerEndPoints endpoints = epMgr.getNodeEndPoints("@").getServerEndPoints("@");
        EndPoint agentEndpoint = endpoints.getEndPoint("DFP_AGENT_ADDRESS");
        if (agentEndpoint == null) {
            Tr.info(TC, Messages._0001I);
            return;
        }
        int agentPort = agentEndpoint.getPort();
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(agentPort);
        } catch (IOException ex) {
            // TODO: test the behavior if port is already in use
            throw new RuntimeError(ex);
        }
        EndPoint httpEndpoint = endpoints.getEndPoint("WC_defaulthost");
        agent = new Agent(serverSocket, httpEndpoint.getPort());
        new Thread(agent, "DFP Acceptor Thread").start();
        Tr.info(TC, Messages._0002I, agentPort);
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
