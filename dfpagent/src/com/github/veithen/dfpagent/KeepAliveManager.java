package com.github.veithen.dfpagent;

import com.github.veithen.dfpagent.resources.Messages;
import com.ibm.ejs.ras.Tr;
import com.ibm.ejs.ras.TraceComponent;

public class KeepAliveManager implements Runnable {
    private static final TraceComponent TC = Tr.register(KeepAliveManager.class, Constants.TRACE_GROUP, Messages.class.getName());
    
    private final Connection connection;
    private long lastKeepAlive;
    private int interval;
    private boolean stopping;

    public KeepAliveManager(Connection connection) {
        this.connection = connection;
    }
    
    public synchronized void setKeepAlive(int keepAlive) {
        // keepAlive is in seconds, but interval is in milliseconds.
        // Note that keepAlive is the maximum interval. We use an interval
        // that is 3/4 of the keepAlive.
        interval = keepAlive*1000*3/4;
        notifyAll();
    }
    
    public synchronized void stop() {
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
                        connection.keepAlive();
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
