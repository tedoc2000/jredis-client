package com.zibobo.yedis.protocol.unified.sets;

import com.zibobo.yedis.ops.BytesSetReplyCallback;
import com.zibobo.yedis.ops.sets.SdiffOperation;
import com.zibobo.yedis.protocol.unified.BytesSetReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.VarArgsCommand;

public class SdiffOperationImpl extends BytesSetReplyOperationImpl implements
        SdiffOperation {
    private static final VarArgsCommand CMD = new VarArgsCommand("SDIFF");
    private final byte[][] keys;

    public SdiffOperationImpl(byte[][] keys, BytesSetReplyCallback cb) {
        super(cb);
        this.keys = keys;
    }

    @Override
    public void initialize() {
        setVargsArguments(CMD, keys);
    }

}
