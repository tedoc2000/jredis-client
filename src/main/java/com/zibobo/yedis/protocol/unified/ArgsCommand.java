package com.zibobo.yedis.protocol.unified;

public abstract class ArgsCommand extends Command {

    protected ArgsCommand(String cmd) {
        super(cmd);
    }

    public abstract int getArgCountSize(byte[]... args);

    public abstract byte[] getArgsCountBytes(byte[]... args);

}
