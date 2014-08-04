package com.zibobo.yedis;

public enum Aggregation {
    SUM(new byte[] { 'S', 'U', 'M' }), MIN(new byte[] { 'M', 'I', 'N' }), MAX(
            new byte[] { 'M', 'A', 'X' });

    public final byte[] value;

    private Aggregation(byte[] value) {
        this.value = value;
    }
}
