package com.zibobo.yedis.protocol.unified;

import com.zibobo.yedis.util.IntegerToBytesUtils;

public class FixedArgsCommand extends ArgsCommand {

    private final byte[] argCountBytes;

    public FixedArgsCommand(String cmd, int argCount) {
        super(cmd);
        argCountBytes = IntegerToBytesUtils.toBytes(argCount + 1);
    }

    @Override
    public int getArgCountSize(byte[]... args) {
        return argCountBytes.length;
    }

    @Override
    public byte[] getArgsCountBytes(byte[]... args) {
        return argCountBytes;
    }

}
