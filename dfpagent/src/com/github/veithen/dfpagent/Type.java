package com.github.veithen.dfpagent;

public enum Type {
    SECURITY(0x0001, "Security"),
    LOAD(0x0002, "Load"),
    KEEP_ALIVE(0x0101, "Keep-Alive"),
    BINDID_TABLE(0x0301, "BindID Table");
    
    private final int code;
    private final String name;
    
    private Type(int code, String name) {
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
