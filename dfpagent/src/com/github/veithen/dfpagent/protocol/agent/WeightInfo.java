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

import java.net.Inet4Address;

import com.github.veithen.dfpagent.protocol.Protocol;

/**
 * Contains weight information for a given address and port.
 */
public class WeightInfo {
    private final Inet4Address address;
    private final Protocol protocol;
    private final int port;
    private final int weight;
    
    /**
     * Constructor.
     * 
     * @param address
     *            the IP address; note that only IPv4 is supported by DFP
     * @param protocol
     *            the protocol
     * @param port
     *            the port number
     * @param weight
     *            the weight; must be a value between 0 and 65535
     */
    public WeightInfo(Inet4Address address, Protocol protocol, int port, int weight) {
        this.address = address;
        this.protocol = protocol;
        this.port = port;
        this.weight = weight;
    }

    public Inet4Address getAddress() {
        return address;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public int getPort() {
        return port;
    }

    public int getWeight() {
        return weight;
    }
}
