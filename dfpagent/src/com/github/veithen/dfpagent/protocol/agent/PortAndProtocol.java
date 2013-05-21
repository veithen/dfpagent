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
