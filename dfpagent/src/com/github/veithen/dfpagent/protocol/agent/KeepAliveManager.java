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

import com.github.veithen.dfpagent.Constants;
import com.github.veithen.dfpagent.resources.Messages;
import com.ibm.ejs.ras.Tr;
import com.ibm.ejs.ras.TraceComponent;

/**
 * Schedules keep alives for a given {@link Peer}.
 */
final class KeepAliveManager implements Runnable {
    private static final TraceComponent TC = Tr.register(KeepAliveManager.class, Constants.TRACE_GROUP, Messages.class.getName());
    
    private final Peer peer;
    private long lastKeepAlive;
    private int interval;
    private boolean stopping;

    KeepAliveManager(Peer peer) {
        this.peer = peer;
    }
    
    synchronized void setKeepAlive(int keepAlive) {
        // keepAlive is in seconds, but interval is in milliseconds.
        // Note that keepAlive is the maximum interval. We use an interval
        // that is 3/4 of the keepAlive.
        interval = keepAlive*1000*3/4;
        notifyAll();
    }
    
    synchronized void stop() {
        stopping = true;
        notifyAll();
    }
    
    public synchronized void run() {
        lastKeepAlive = System.currentTimeMillis();
        try {
            while (!stopping) {
                if (interval == 0) {
                    if (TC.isDebugEnabled()) {
                        Tr.debug(TC, "Keep-alive not enabled; waiting for signal");
                    }
                    wait();
                } else {
                    long currentTime = System.currentTimeMillis();
                    long delay = lastKeepAlive+interval-currentTime;
                    if (delay <= 0) {
                        if (TC.isDebugEnabled()) {
                            Tr.debug(TC, "Triggering keep-alive");
                        }
                        try {
                            peer.sendPreferenceInformation();
                        } catch (Throwable ex) {
                            Tr.error(TC, Messages._0012E, ex);
                        }
                        lastKeepAlive = currentTime;
                    } else {
                        if (TC.isDebugEnabled()) {
                            Tr.debug(TC, "Sleeping for {0} milliseconds", delay);
                        }
                        wait(delay);
                    }
                }
            }
        } catch (InterruptedException ex) {
            // Ignore: thread will be stopped
        }
        if (TC.isDebugEnabled()) {
            Tr.debug(TC, "Keep-alive thread stopping");
        }
    }
}
