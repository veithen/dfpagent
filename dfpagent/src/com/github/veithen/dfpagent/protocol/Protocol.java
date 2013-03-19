package com.github.veithen.dfpagent.protocol;

public enum Protocol {
    // Note: the codes are not specified by the DFP specification; a test with CSS has shown
    // that the expected codes are the ones defined in /etc/protocols
    TCP(6);
    
    private final int code;

    private Protocol(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
