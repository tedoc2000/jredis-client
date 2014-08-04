package com.zibobo.yedis.transcoder;

import com.zibobo.yedis.util.LongToBytesUtils;

public class LongTranscoder implements Transcoder<Long> {

    private static final LongTranscoder INSTANCE = new LongTranscoder();

    public static final LongTranscoder getInstance() {
        return INSTANCE;
    }

    private LongTranscoder() {

    }

    @Override
    public boolean asyncDecode(byte[] d) {
        return false;
    }

    @Override
    public byte[] encode(Long o) {
        return LongToBytesUtils.toBytes(o);
    }

    @Override
    public Long decode(byte[] d) {
        if (d == null) {
            return null;
        }
        return LongToBytesUtils.parseLong(d, 10);
    }

}
