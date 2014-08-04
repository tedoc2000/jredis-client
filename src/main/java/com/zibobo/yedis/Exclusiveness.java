package com.zibobo.yedis;

public enum Exclusiveness {
    NX(new byte[] { 'N', 'X' }), XX(new byte[] { 'X', 'X' });

    public final byte[] value;

    private Exclusiveness(byte[] value) {
        this.value = value;
    }
}