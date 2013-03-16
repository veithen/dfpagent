package com.github.veithen.dfpagent.protocol.connection;

import java.io.IOException;

import com.github.veithen.dfpagent.protocol.message.Message;

/**
 * Handles messages received over a DFP connection.
 */
public interface Handler {
    void processMessage(Message message) throws IOException;
}
