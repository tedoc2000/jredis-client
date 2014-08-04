package com.zibobo.yedis.protocol.unified.sets;

import com.zibobo.yedis.ops.BytesSetReplyCallback;
import com.zibobo.yedis.ops.sets.SinterOperation;
import com.zibobo.yedis.protocol.unified.BytesSetReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.VarArgsCommand;

public class SinterOperationImpl extends BytesSetReplyOperationImpl implements
        SinterOperation {
    private static final VarArgsCommand CMD = new VarArgsCommand("SINTER");
    private final byte[][] keys;

    public SinterOperationImpl(byte[][] keys, BytesSetReplyCallback cb) {
        super(cb);
        this.keys = keys;
    }

    @Override
    public void initialize() {
        setVargsArguments(CMD, keys);
    }

}
