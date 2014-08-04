package com.zibobo.yedis.protocol.unified;

import com.zibobo.yedis.util.IntegerToBytesUtils;

public class OptionalArgsCommand extends ArgsCommand {

    public OptionalArgsCommand(String cmd) {
        super(cmd);
    }

    @Override
    public int getArgCountSize(byte[]... args) {
        return IntegerToBytesUtils.bytesSize(args.length + 1);
    }

    @Override
    public byte[] getArgsCountBytes(byte[]... args) {
        return IntegerToBytesUtils.toBytes(args.length + 1);
    }

}
