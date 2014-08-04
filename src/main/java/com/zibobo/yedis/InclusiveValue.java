package com.zibobo.yedis;

import com.zibobo.yedis.util.DoubleToBytesUtils;

public class InclusiveValue extends IntervalValue {

    public InclusiveValue(double value) {
        super(value);
    }

    @Override
    public byte[] toBytes() {
        return DoubleToBytesUtils.toBytes(value);
    }

}
