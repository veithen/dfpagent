package com.github.veithen.dfpagent;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;

public class TLV {
    private final Type type;
    private final byte[] value;
    
    public TLV(Type type, byte[] value) {
        this.type = type;
        this.value = value;
    }

    public Type getType() {
        return type;
    }
    
    public int getDataLength() {
        return value.length;
    }
    
    public DataInput getValue() {
        return new DataInputStream(new ByteArrayInputStream(value));
    }
    
    public void writeValue(DataOutput out) throws IOException {
        out.write(value);
    }
}
