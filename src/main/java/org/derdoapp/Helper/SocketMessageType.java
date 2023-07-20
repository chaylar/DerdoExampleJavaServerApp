package org.derdoapp.Helper;

public enum SocketMessageType {

    MESSAGE("message"),
    GREETING("greeting");

    private String code;

    public String getCode() {
        return this.code;
    }

    private SocketMessageType(String code) {
        this.code = code;
    }
}
