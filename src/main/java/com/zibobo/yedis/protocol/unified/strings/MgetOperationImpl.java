package com.zibobo.yedis.protocol.unified.strings;

import com.zibobo.yedis.ops.BytesListReplyCallback;
import com.zibobo.yedis.ops.strings.MgetOperation;
import com.zibobo.yedis.protocol.unified.BytesListReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.VarArgsCommand;

public class MgetOperationImpl extends BytesListReplyOperationImpl implements
        MgetOperation {
    private static final VarArgsCommand CMD = new VarArgsCommand("MGET");

    private final byte[][] keys;

    public MgetOperationImpl(byte[][] keys, BytesListReplyCallback cb) {
        super(cb);
        this.keys = keys;
    }

    @Override
    public void initialize() {
        setVargsArguments(CMD, keys);

    }

}
