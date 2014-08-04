package com.zibobo.yedis.protocol.unified.sets;

import com.zibobo.yedis.ops.BytesSetReplyCallback;
import com.zibobo.yedis.ops.sets.SunionOperation;
import com.zibobo.yedis.protocol.unified.BytesSetReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.VarArgsCommand;

public class SunionOperationImpl extends BytesSetReplyOperationImpl implements
        SunionOperation {
    private static final VarArgsCommand CMD = new VarArgsCommand("SUNION");
    private final byte[][] keys;

    public SunionOperationImpl(byte[][] keys, BytesSetReplyCallback cb) {
        super(cb);
        this.keys = keys;
    }

    @Override
    public void initialize() {
        setVargsArguments(CMD, keys);
    }

}
