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

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Prepares the value of a {@link TLV}.
 */
public final class ValueWriter implements DataOutput {
    private final TLV tlv;
    private final ByteArrayOutputStream baos;
    private final DataOutput out;
    private boolean committed;

    ValueWriter(TLV tlv) {
        this.tlv = tlv;
        baos = new ByteArrayOutputStream();
        out = new DataOutputStream(baos);
    }
    
    private void checkState() {
        if (committed) {
            throw new IllegalStateException("Value has already been committed");
        }
    }
    
    public void write(int b) throws IOException {
        checkState();
        out.write(b);
    }

    public void write(byte[] b) throws IOException {
        checkState();
        out.write(b);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        checkState();
        out.write(b, off, len);
    }

    public void writeBoolean(boolean v) throws IOException {
        checkState();
        out.writeBoolean(v);
    }

    public void writeByte(int v) throws IOException {
        checkState();
        out.writeByte(v);
    }

    public void writeShort(int v) throws IOException {
        checkState();
        out.writeShort(v);
    }

    public void writeChar(int v) throws IOException {
        checkState();
        out.writeChar(v);
    }

    public void writeInt(int v) throws IOException {
        checkState();
        out.writeInt(v);
    }

    public void writeLong(long v) throws IOException {
        checkState();
        out.writeLong(v);
    }

    public void writeFloat(float v) throws IOException {
        checkState();
        out.writeFloat(v);
    }

    public void writeDouble(double v) throws IOException {
        checkState();
        out.writeDouble(v);
    }

    public void writeBytes(String s) throws IOException {
        checkState();
        out.writeBytes(s);
    }

    public void writeChars(String s) throws IOException {
        checkState();
        out.writeChars(s);
    }

    public void writeUTF(String str) throws IOException {
        checkState();
        out.writeUTF(str);
    }
    
    public void commit() {
        checkState();
        tlv.setValue(baos.toByteArray());
        committed = true;
    }
}
