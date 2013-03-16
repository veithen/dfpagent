package com.github.veithen.dfpagent.protocol.message;

import com.github.veithen.dfpagent.protocol.tlv.Type;

/**
 * Indicates that a required TLV was missing in the DFP message.
 */
public class MissingTLVException extends Exception {
    private static final long serialVersionUID = 5237302085545469069L;
    
    private final MessageType messageType;
    private final Type type;
    
    public MissingTLVException(MessageType messageType, Type type) {
        super("Missing " + type + " in " + messageType);
        this.messageType = messageType;
        this.type = type;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public Type getType() {
        return type;
    }
}
