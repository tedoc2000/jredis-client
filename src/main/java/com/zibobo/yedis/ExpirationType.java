package com.zibobo.yedis;

public enum ExpirationType {
    EX(new byte[] { 'E', 'X' }), PX(new byte[] { 'P', 'X' });
    public final byte[] value;

    private ExpirationType(byte[] value) {
        this.value = value;
    }
}