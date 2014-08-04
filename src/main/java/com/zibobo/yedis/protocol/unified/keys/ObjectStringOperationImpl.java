package com.zibobo.yedis.protocol.unified.keys;

import com.zibobo.yedis.ops.StringReplyCallback;
import com.zibobo.yedis.ops.keys.ObjectStringOperation;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.protocol.unified.StringReplyOperationImpl;

public class ObjectStringOperationImpl extends StringReplyOperationImpl
        implements ObjectStringOperation {

    private static final FixedArgsCommand CMD = new FixedArgsCommand("OBJECT",
            2);

    private final byte[] cmd;
    private final byte[] key;

    public ObjectStringOperationImpl(byte[] cmd, byte[] key,
            StringReplyCallback cb) {
        super(cb);
        this.cmd = cmd;
        this.key = key;
    }

    @Override
    public void initialize() {
        setArguments(CMD, cmd, key);
    }

}
