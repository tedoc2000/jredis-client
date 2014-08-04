package com.zibobo.yedis.transcoder;

import java.nio.charset.Charset;

public class StringTranscoder implements Transcoder<String> {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    private static final StringTranscoder INSTANCE = new StringTranscoder();

    public static StringTranscoder getInstance() {
        return INSTANCE;

    }

    protected StringTranscoder() {

    }

    @Override
    public boolean asyncDecode(byte[] d) {
        return false;
    }

    @Override
    public byte[] encode(String o) {
        return o.getBytes(UTF8);
    }

    @Override
    public String decode(byte[] d) {
        if (d == null) {
            return null;
        }
        return new String(d, UTF8);
    }

}
