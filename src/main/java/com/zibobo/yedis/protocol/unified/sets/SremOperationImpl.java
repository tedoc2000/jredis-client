package com.zibobo.yedis.protocol.unified.sets;

import com.zibobo.yedis.ops.LongReplyCallback;
import com.zibobo.yedis.ops.sets.SremOperation;
import com.zibobo.yedis.protocol.unified.LongReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.VarArgsCommand;

public class SremOperationImpl extends LongReplyOperationImpl implements
        SremOperation {
    private static final VarArgsCommand CMD = new VarArgsCommand("SREM");
    private final byte[] key;
    private final byte[][] values;

    public SremOperationImpl(byte[] key, byte[][] values, LongReplyCallback cb) {
        super(cb);
        this.key = key;
        this.values = values;
    }

    @Override
    public void initialize() {
        setVargsArguments(CMD, values, key);
    }

}
