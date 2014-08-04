package com.zibobo.yedis.protocol.unified.response;

import java.nio.ByteBuffer;

import com.zibobo.yedis.exception.RedisIOException;

public class ByteParser extends AbstractParser {

    private final byte[] byteData;
    private int readOffset;
    private boolean foundCr;

    public ByteParser(int length) {
        super();
        byteData = new byte[length];
    }

    @Override
    public void parse(ByteBuffer data) {
        if (readOffset == byteData.length) {
            while (data.remaining() > 0) {
                byte b = data.get();
                if (b == '\r') {
                    foundCr = true;
                } else if (b == '\n') {
                    assert foundCr : "got a \\n without a \\r";
                    done = true;
                    return;
                } else {
                    throw new RedisIOException("Invalid response from server");
                }
            }
        }
        int toRead = byteData.length - readOffset;
        int available = data.remaining();
        toRead = Math.min(toRead, available);
        data.get(byteData, readOffset, toRead);
        readOffset += toRead;
    }

    public byte[] getResults() {
        return byteData;
    }

}
