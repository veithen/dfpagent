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
package com.github.veithen.dfpagent.protocol.connection;

import java.io.IOException;

import com.github.veithen.dfpagent.Constants;
import com.github.veithen.dfpagent.protocol.message.Message;
import com.github.veithen.dfpagent.protocol.tlv.TLV;
import com.github.veithen.dfpagent.resources.Messages;
import com.ibm.ejs.ras.Tr;
import com.ibm.ejs.ras.TraceComponent;

public class LogInterceptor implements Interceptor{
    private static final TraceComponent TC = Tr.register(LogInterceptor.class, Constants.TRACE_GROUP, Messages.class.getName());
    
    private final String label;

    public LogInterceptor(String label) {
        this.label = label;
    }

    public void processMessage(Message message, Direction direction) throws IOException {
        if (TC.isDebugEnabled()) {
            StringBuilder dump = new StringBuilder();
            for (TLV tlv : message) {
                dump.append("== ");
                dump.append(tlv.getType().toString());
                dump.append(" ==\n");
                byte[] value = tlv.toByteArray();
                hexDump(dump, value, value.length);
            }
            Tr.debug(TC, "[{0}] messageType={1}, direction={2}\n{3}", new Object[] { label, message.getType(), direction, dump });
        }
    }

    private static void hexDump(StringBuilder buffer, byte[] data, int length) {
        for (int start = 0; start < length; start += 16) {
            for (int i=0; i<16; i++) {
                if (i == 8) {
                    buffer.append(' ');
                }
                int index = start+i;
                if (index < length) {
                    String hex = Integer.toHexString(data[start+i] & 0xFF);
                    if (hex.length() == 1) {
                        buffer.append('0');
                    }
                    buffer.append(hex);
                } else {
                    buffer.append("  ");
                }
                buffer.append(' ');
            }
            buffer.append(" |");
            for (int i=0; i<16; i++) {
                int index = start+i;
                if (index < length) {
                    int b = data[index] & 0xFF;
                    if (32 <= b && b < 128) {
                        buffer.append((char)b);
                    } else {
                        buffer.append('.');
                    }
                } else {
                    buffer.append(' ');
                }
            }
            buffer.append('|');
            buffer.append('\n');
        }
    }
}
