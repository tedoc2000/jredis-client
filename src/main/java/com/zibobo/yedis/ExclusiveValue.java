package com.zibobo.yedis;

import com.zibobo.yedis.util.DoubleToBytesUtils;

public class ExclusiveValue extends IntervalValue {

    public ExclusiveValue(double value) {
        super(value);
    }

    @Override
    public byte[] toBytes() {
        byte[] doubleBytes =
                DoubleToBytesUtils.toBytes(value);
        byte[] retVal = new byte[doubleBytes.length + 1];
        retVal[0] = (byte) '(';
        System.arraycopy(doubleBytes, 0, retVal, 1, doubleBytes.length);
        return retVal;
    }

}
