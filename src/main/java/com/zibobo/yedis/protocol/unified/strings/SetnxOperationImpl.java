package com.zibobo.yedis.protocol.unified.strings;

import com.zibobo.yedis.ops.BooleanReplyCallback;
import com.zibobo.yedis.ops.strings.SetnxOperation;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.protocol.unified.OKBooleanReplyOperationImpl;

public class SetnxOperationImpl extends OKBooleanReplyOperationImpl implements
        SetnxOperation {

    private static final FixedArgsCommand CMD =
            new FixedArgsCommand("SETNX", 2);

    private final byte[] key;
    private final byte[] value;

    public SetnxOperationImpl(byte[] key, byte[] value, BooleanReplyCallback cb) {
        super(cb);
        this.key = key;
        this.value = value;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key, value);
    }

}
