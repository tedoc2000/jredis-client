package com.zibobo.yedis.protocol.unified.keys;

import com.zibobo.yedis.ops.IntegerReplyCallback;
import com.zibobo.yedis.ops.keys.ObjectIntegerOperation;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.protocol.unified.IntReplyOperationImpl;

public class ObjectIntegerOperationImpl extends IntReplyOperationImpl implements
        ObjectIntegerOperation {

    private static final FixedArgsCommand CMD = new FixedArgsCommand("OBJECT",
            2);

    private final byte[] cmd;
    private final byte[] key;

    public ObjectIntegerOperationImpl(byte[] cmd, byte[] key,
            IntegerReplyCallback cb) {
        super(cb);
        this.cmd = cmd;
        this.key = key;
    }

    @Override
    public void initialize() {
        setArguments(CMD, cmd, key);
    }

}
