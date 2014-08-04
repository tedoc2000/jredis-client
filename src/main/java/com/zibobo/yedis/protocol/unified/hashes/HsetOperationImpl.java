package com.zibobo.yedis.protocol.unified.hashes;

import com.zibobo.yedis.ops.BooleanReplyCallback;
import com.zibobo.yedis.ops.hashes.HsetOperation;
import com.zibobo.yedis.protocol.unified.BooleanReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;

public class HsetOperationImpl extends BooleanReplyOperationImpl implements
        HsetOperation {
    private static final FixedArgsCommand CMD = new FixedArgsCommand("HSET", 3);
    private final byte[] key;
    private final byte[] field;
    private final byte[] value;

    public HsetOperationImpl(byte[] key, byte[] field, byte[] value,
            BooleanReplyCallback cb) {
        super(cb);
        this.key = key;
        this.field = field;
        this.value = value;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key, field, value);
    }

}
