package com.github.veithen.dfpagent.protocol.connection;

import java.io.IOException;

import com.github.veithen.dfpagent.protocol.message.Message;

public interface Interceptor {
    public enum Direction { INBOUND, OUTBOUND };
    
    void processMessage(Message message, Direction direction) throws IOException;
}
