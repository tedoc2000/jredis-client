package com.zibobo.yedis.protocol.unified.keys;

import com.zibobo.yedis.ops.IntegerReplyCallback;
import com.zibobo.yedis.ops.keys.DelOperation;
import com.zibobo.yedis.protocol.unified.IntReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.VarArgsCommand;

public class DelOperationImpl extends IntReplyOperationImpl implements
        DelOperation {
    private static final VarArgsCommand CMD = new VarArgsCommand("DEL");

    private final byte[][] keys;

    public DelOperationImpl(byte[][] keys, IntegerReplyCallback cb) {
        super(cb);
        this.keys = keys;
    }

    @Override
    public void initialize() {
        setVargsArguments(CMD, keys);
    }

}
