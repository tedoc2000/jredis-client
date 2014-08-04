package com.zibobo.yedis.protocol.unified.hashes;

import com.zibobo.yedis.ops.BytesReplyCallback;
import com.zibobo.yedis.ops.hashes.HgetOperation;
import com.zibobo.yedis.protocol.unified.BytesReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;

public class HgetOperationImpl extends BytesReplyOperationImpl implements
        HgetOperation {

    private static final FixedArgsCommand CMD = new FixedArgsCommand("HGET", 2);
    private final byte[] key;
    private final byte[] field;

    public HgetOperationImpl(byte[] key, byte[] field, BytesReplyCallback cb) {
        super(cb);
        this.key = key;
        this.field = field;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key, field);
    }

}
