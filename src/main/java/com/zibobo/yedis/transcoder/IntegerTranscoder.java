package com.zibobo.yedis.transcoder;

import com.zibobo.yedis.util.IntegerToBytesUtils;

public class IntegerTranscoder implements Transcoder<Integer> {

    private static final IntegerTranscoder INSTANCE = new IntegerTranscoder();

    public static IntegerTranscoder getInstance() {
        return INSTANCE;
    }

    private IntegerTranscoder() {

    }

    @Override
    public boolean asyncDecode(byte[] d) {
        return false;
    }

    @Override
    public byte[] encode(Integer o) {
        return IntegerToBytesUtils.toBytes(o);
    }

    @Override
    public Integer decode(byte[] d) {
        if (d == null) {
            return null;
        }
        return IntegerToBytesUtils.parseInt(d, 10);
    }

}
