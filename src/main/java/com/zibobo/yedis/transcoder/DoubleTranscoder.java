package com.zibobo.yedis.transcoder;

import java.nio.charset.Charset;

import com.zibobo.yedis.util.DoubleToBytesUtils;

public class DoubleTranscoder implements Transcoder<Double> {
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final DoubleTranscoder INSTANCE = new DoubleTranscoder();

    public static DoubleTranscoder getInstance() {
        return INSTANCE;
    }

    private DoubleTranscoder() {

    }

    @Override
    public boolean asyncDecode(byte[] d) {
        return false;
    }

    @Override
    public byte[] encode(Double o) {
        return DoubleToBytesUtils.toBytes(o);
    }

    @Override
    public Double decode(byte[] d) {
        if (d == null) {
            return null;
        }
        return Double.valueOf(new String(d, UTF_8));
    }

}
