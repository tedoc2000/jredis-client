package com.zibobo.yedis.protocol.unified.sets;

import com.zibobo.yedis.ops.LongReplyCallback;
import com.zibobo.yedis.ops.sets.SaddOperation;
import com.zibobo.yedis.protocol.unified.LongReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.VarArgsCommand;

public class SaddOperationImpl extends LongReplyOperationImpl implements
        SaddOperation {
    private static final VarArgsCommand CMD = new VarArgsCommand("SADD");
    private final byte[] key;
    private final byte[][] values;

    public SaddOperationImpl(byte[] key, byte[][] values, LongReplyCallback cb) {
        super(cb);
        this.key = key;
        this.values = values;
    }

    @Override
    public void initialize() {
        setVargsArguments(CMD, values, key);
    }

}
