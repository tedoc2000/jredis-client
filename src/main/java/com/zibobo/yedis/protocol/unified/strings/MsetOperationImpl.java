package com.zibobo.yedis.protocol.unified.strings;

import com.zibobo.yedis.ops.OperationCallback;
import com.zibobo.yedis.ops.strings.MsetOperation;
import com.zibobo.yedis.protocol.unified.IgnoreStatusReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.VarArgsCommand;

public class MsetOperationImpl extends IgnoreStatusReplyOperationImpl implements
        MsetOperation {

    private static final VarArgsCommand CMD = new VarArgsCommand("MSET");

    private final byte[][] keyValues;

    public MsetOperationImpl(byte[][] keyValues, OperationCallback cb) {
        super(cb);
        this.keyValues = keyValues;
    }

    @Override
    public void initialize() {
        setVargsArguments(CMD, keyValues);

    }

}
