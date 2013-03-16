package com.github.veithen.dfpagent.protocol.tlv;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;

public final class TLV {
    private final Type type;
    private byte[] value;
    
    public TLV(Type type, byte[] value) {
        this.type = type;
        this.value = value;
    }
    
    /**
     * Create a new TLV with uninitialized value. Use this constructor to build a TLV for an
     * outgoing message. Use {@link #getValueWriter()} to initialize the value of the TLV.
     * 
     * @param type
     *            the type of the TLV
     */
    public TLV(Type type) {
        this(type, null);
    }

    public Type getType() {
        return type;
    }
    
    public int getDataLength() {
        return value.length;
    }
    
    public ValueWriter getValueWriter() {
        return new ValueWriter(this);
    }
    
    void setValue(byte[] value) {
        if (this.value != null) {
            throw new IllegalStateException("Value already set");
        }
        this.value = value;
    }

    public DataInput getValue() {
        return new DataInputStream(new ByteArrayInputStream(value));
    }
    
    public void writeValue(DataOutput out) throws IOException {
        out.write(value);
    }
}
