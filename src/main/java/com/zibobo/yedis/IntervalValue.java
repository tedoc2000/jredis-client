package com.zibobo.yedis;

public abstract class IntervalValue {

    public final double value;

    public IntervalValue(double value) {
        this.value = value;
    }

    abstract public byte[] toBytes();
}
