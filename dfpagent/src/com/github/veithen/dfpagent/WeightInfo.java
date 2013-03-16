package com.github.veithen.dfpagent;

import java.net.Inet4Address;

/**
 * Contains weight information for a given address and port.
 */
public class WeightInfo {
    private final Inet4Address address;
    private final int port;
    private final int weight;
    
    /**
     * Constructor.
     * 
     * @param address
     *            the IP address; note that only IPv4 is supported by DFP
     * @param port
     *            the port number
     * @param weight
     *            the weight; must be a value between 0 and 65535
     */
    public WeightInfo(Inet4Address address, int port, int weight) {
        this.address = address;
        this.port = port;
        this.weight = weight;
    }

    public Inet4Address getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public int getWeight() {
        return weight;
    }
}
