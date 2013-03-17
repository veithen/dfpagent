package com.github.veithen.dfpagent.protocol.message;

public enum MessageType {
    PREFERENCE_INFORMATION(0x0101, "Preference Information"),
    SERVER_STATE(0x0201, "Server State"),
    DFP_PARAMETERS(0x0301, "DFP Parameters"),
    BINDID_REQUEST(0x0401, "BindID Request"),
    BINDID_REPORT(0x0402, "BindID Report");
    
    private final int code;
    private final String name;
    
    private MessageType(int code, String name) {
        this.code = code;
        this.name = name;
    }
    
    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
