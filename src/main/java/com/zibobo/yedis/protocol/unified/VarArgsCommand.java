package com.zibobo.yedis.protocol.unified;

import com.zibobo.yedis.util.IntegerToBytesUtils;

public class VarArgsCommand extends Command {

    public VarArgsCommand(String cmd) {
        super(cmd);
    }

    public int getArgCountSize(byte[][] vargs, byte[]... args) {
        return IntegerToBytesUtils.bytesSize(vargs.length + args.length + 1);
    }

    public byte[] getArgsCountBytes(byte[][] vargs, byte[]... args) {
        return IntegerToBytesUtils.toBytes(vargs.length + args.length + 1);
    }
}
