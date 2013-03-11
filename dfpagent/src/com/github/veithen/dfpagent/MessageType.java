package com.github.veithen.dfpagent;

public enum MessageType {
    PREFERENCE_INFORMATION(0x0101, "Preference Information"),
    DFP_PARAMETERS(0x0301, "DFP Parameters"),
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
