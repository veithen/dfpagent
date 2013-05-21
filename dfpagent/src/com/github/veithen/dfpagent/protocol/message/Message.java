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
package com.github.veithen.dfpagent.protocol.message;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.github.veithen.dfpagent.protocol.tlv.TLV;
import com.github.veithen.dfpagent.protocol.tlv.Type;

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
    
    public Message(MessageType type, TLV... tlvs) {
        this(type, Arrays.asList(tlvs));
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
