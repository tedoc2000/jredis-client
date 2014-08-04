package com.zibobo.yedis.protocol.unified.hashes;

import com.zibobo.yedis.ops.IntegerReplyCallback;
import com.zibobo.yedis.ops.hashes.HlenOperation;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.protocol.unified.IntReplyOperationImpl;

public class HlenOperationImpl extends IntReplyOperationImpl implements
        HlenOperation {
    private final static FixedArgsCommand CMD = new FixedArgsCommand("HLEN", 1);

    private final byte[] key;

    public HlenOperationImpl(byte[] key, IntegerReplyCallback cb) {
        super(cb);
        this.key = key;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key);
    }

}
