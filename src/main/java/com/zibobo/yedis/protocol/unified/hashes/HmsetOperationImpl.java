package com.zibobo.yedis.protocol.unified.hashes;

import com.zibobo.yedis.ops.OperationCallback;
import com.zibobo.yedis.ops.hashes.HmsetOperation;
import com.zibobo.yedis.protocol.unified.IgnoreStatusReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.VarArgsCommand;

public class HmsetOperationImpl extends IgnoreStatusReplyOperationImpl
        implements HmsetOperation {

    private static final VarArgsCommand CMD = new VarArgsCommand("HMSET");
    private final byte[] key;
    private final byte[][] fieldValues;

    public HmsetOperationImpl(byte[] key, byte[][] fieldValues,
            OperationCallback cb) {
        super(cb);
        this.key = key;
        this.fieldValues = fieldValues;
    }

    @Override
    public void initialize() {
        setVargsArguments(CMD, fieldValues, key);
    }

}
