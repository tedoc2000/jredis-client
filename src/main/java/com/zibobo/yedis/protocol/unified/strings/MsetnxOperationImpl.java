package com.zibobo.yedis.protocol.unified.strings;

import com.zibobo.yedis.ops.BooleanReplyCallback;
import com.zibobo.yedis.ops.strings.MsetnxOperation;
import com.zibobo.yedis.protocol.unified.BooleanReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.VarArgsCommand;

public class MsetnxOperationImpl extends BooleanReplyOperationImpl implements
        MsetnxOperation {

    private static final VarArgsCommand CMD = new VarArgsCommand("MSETNX");

    private final byte[][] keyValues;

    public MsetnxOperationImpl(byte[][] keyValues, BooleanReplyCallback cb) {
        super(cb);
        this.keyValues = keyValues;
    }

    @Override
    public void initialize() {
        setVargsArguments(CMD, keyValues);
    }

}
