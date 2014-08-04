package com.zibobo.yedis.transcoder;

public class BytesTranscoder implements Transcoder<byte[]> {

    private static final BytesTranscoder INSTANCE = new BytesTranscoder();

    public static BytesTranscoder getInstance() {
        return INSTANCE;
    }

    protected BytesTranscoder() {

    }

    @Override
    public boolean asyncDecode(byte[] d) {
        return false;
    }

    @Override
    public byte[] encode(byte[] o) {
        return o;
    }

    @Override
    public byte[] decode(byte[] d) {
        return d;
    }

}
