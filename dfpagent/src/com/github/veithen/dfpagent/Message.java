package com.github.veithen.dfpagent;

import java.util.Iterator;
import java.util.List;

/**
 * A DFP message.
 */
public class Message implements Iterable<TLV> {
    private final MessageType type;
    private final List<TLV> tlvs;
    
    public Message(MessageType type, List<TLV> tlvs) {
        this.type = type;
        this.tlvs = tlvs;
    }

    public MessageType getType() {
        return type;
    }
    
    public TLV getRequiredTLV(Type type) throws MissingTLVException {
        for (TLV tlv : tlvs) {
            if (tlv.getType() == type) {
                return tlv;
            }
        }
        throw new MissingTLVException(this.type, type);
    }

    public Iterator<TLV> iterator() {
        return tlvs.iterator();
    }
}
