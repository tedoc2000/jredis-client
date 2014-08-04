package com.zibobo.yedis.protocol.unified;

import java.nio.charset.Charset;

import com.zibobo.yedis.util.IntegerToBytesUtils;

public abstract class Command {
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private final byte[] cmdBytes;
    private final byte[] cmdLengthBytes;

    public Command(String cmd) {
        cmdBytes = cmd.getBytes(UTF_8);
        cmdLengthBytes = IntegerToBytesUtils.toBytes(cmdBytes.length);
    }

    public int getCmdSize() {
        return cmdBytes.length;
    }

    public byte[] getCmdBytes() {
        return cmdBytes;
    }

    public int getCmdLengthSize() {
        return cmdLengthBytes.length;
    }

    public byte[] getCmdLengthBytes() {
        return cmdLengthBytes;
    }

}
