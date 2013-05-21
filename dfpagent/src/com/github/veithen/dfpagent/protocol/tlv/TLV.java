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
    
    // TODO: do we really want this?
    public byte[] toByteArray() {
        return value;
    }
    
    public void writeValue(DataOutput out) throws IOException {
        out.write(value);
    }
}
