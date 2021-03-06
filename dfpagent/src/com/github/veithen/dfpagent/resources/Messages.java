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
package com.github.veithen.dfpagent.resources;

import java.util.ListResourceBundle;

public class Messages extends ListResourceBundle {
    public static final String _0001I = "0001I";
    public static final String _0002I = "0002I";
    public static final String _0003I = "0003I";
    public static final String _0004E = "0004E";
    public static final String _0005E = "0005E";
    public static final String _0006E = "0006E";
    public static final String _0007W = "0007W";
    public static final String _0008E = "0008E";
    public static final String _0009W = "0009W";
    public static final String _0010W = "0010W";
    public static final String _0011E = "0011E";
    public static final String _0012E = "0012E";
    public static final String _0013E = "0013E";
    public static final String _0014I = "0014I";
    public static final String _0015I = "0015I";
    public static final String _0016I = "0016I";
    public static final String _0017I = "0017I";
    public static final String _0018E = "0018E";
    
    private static final Object[][] contents = {
        { _0001I, "DFP0001I: DFP Agent is disabled" },
        { _0002I, "DFP0002I: DFP Agent started on port {0}" },
        { _0003I, "DFP0003I: DFP Agent stopped" },
        { _0004E, "DFP0004E: Unexpected exception while stopping DFP Agent:\n{0}" },
        { _0005E, "DFP0005E: Protocol version mismatch; expected {0} but got {1}" },
        { _0006E, "DFP0006E: Received a malformed DFP message" },
        { _0007W, "DFP0007W: Received unsupported DFP message of type {0}" },
        { _0008E, "DFP0008E: Received a {0} message without {1} TLV" },
        { _0009W, "DFP0009W: Received unknown DFP message of type {0}" },
        { _0010W, "DFP0010W: Received unknown TLV with type {0}" },
        { _0011E, "DFP0011E: Received TLV of type {0} with unexpected data length" },
        { _0012E, "DFP0012E: Keep-alive failed:\n{0}" },
        { _0013E, "DFP0013E: Unexpected exception in accept loop:\n{0}" },
        { _0014I, "DFP0014I: Peer {0} connected" },
        { _0015I, "DFP0015I: Peer {0} disconnected" },
        { _0016I, "DFP0016I: WC_defaulthost is not listening on any IPv4 address" },
        { _0017I, "DFP0017I: Updating server weight. Old value: {0}; new value: {1}" },
        { _0018E, "DFP0018E: Failed to send Preference Information message to peer {0}:\n{1}" },
    };
    
    @Override
    protected Object[][] getContents() {
        return contents;
    }
}
