package com.zibobo.yedis.protocol.unified.hashes;

import com.zibobo.yedis.ops.BytesListReplyCallback;
import com.zibobo.yedis.ops.hashes.HgetallOperation;
import com.zibobo.yedis.protocol.unified.BytesListReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;

public class HgetallOperationImpl extends BytesListReplyOperationImpl implements
        HgetallOperation {

    private static final FixedArgsCommand CMD = new FixedArgsCommand("HGETALL",
            1);
    private final byte[] key;

    public HgetallOperationImpl(byte[] key, BytesListReplyCallback cb) {
        super(cb);
        this.key = key;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key);
    }

}
