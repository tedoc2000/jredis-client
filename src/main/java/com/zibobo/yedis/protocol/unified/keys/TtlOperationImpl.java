package com.zibobo.yedis.protocol.unified.keys;

import com.zibobo.yedis.ops.IntegerReplyCallback;
import com.zibobo.yedis.ops.keys.TtlOperation;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.protocol.unified.IntReplyOperationImpl;

public class TtlOperationImpl extends IntReplyOperationImpl implements
        TtlOperation {

    private static final FixedArgsCommand CMD = new FixedArgsCommand("TTL", 1);
    private final byte[] key;

    public TtlOperationImpl(byte[] key, IntegerReplyCallback cb) {
        super(cb);
        this.key = key;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key);
    }

}
