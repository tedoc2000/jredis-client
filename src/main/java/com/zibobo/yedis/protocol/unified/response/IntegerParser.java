package com.zibobo.yedis.protocol.unified.response;

import java.nio.ByteBuffer;

import com.zibobo.yedis.exception.RedisIOException;

public class IntegerParser implements Parser {
    protected long length = 0;
    protected boolean isNegative = false;
    protected boolean foundCr = false;
    protected boolean done = false;

    @Override
    public void parse(ByteBuffer data) {
        while (data.remaining() > 0) {
            byte b = data.get();
            if (b == '\r') {
                foundCr = true;
            } else if (b == '\n') {
                assert foundCr : "got a \\n without a \\r";
                done = true;
                if (isNegative) {
                    length = -length;
                }
                return;
            } else {
                assert !foundCr : "got a \\r without a \\n";
                if (b == '-') {
                    if (length == 0) {
                        isNegative = true;
                        continue;
                    } else {
                        throw new RedisIOException("Unexpected - in response");
                    }
                }
                length = (10 * length) + (b - '0');
            }
        }

    }

    @Override
    public boolean done() {
        return done;
    }

    public long getResults() {
        return length;
    }

}
