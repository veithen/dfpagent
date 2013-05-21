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
