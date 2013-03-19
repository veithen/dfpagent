package com.github.veithen.dfpagent.protocol.agent;

import com.github.veithen.dfpagent.protocol.Protocol;

final class PortAndProtocol {
    private final int port;
    private final Protocol protocol;
    
    PortAndProtocol(int port, Protocol protocol) {
        this.port = port;
        this.protocol = protocol;
    }

    int getPort() {
        return port;
    }

    Protocol getProtocol() {
        return protocol;
    }

    @Override
    public int hashCode() {
        return port + 31*protocol.getCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PortAndProtocol) {
            PortAndProtocol other = (PortAndProtocol)obj;
            return other.port == port && other.protocol == protocol;
        } else {
            return false;
        }
    }
}
