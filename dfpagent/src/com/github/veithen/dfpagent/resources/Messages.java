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
    };
    
    @Override
    protected Object[][] getContents() {
        return contents;
    }
}
