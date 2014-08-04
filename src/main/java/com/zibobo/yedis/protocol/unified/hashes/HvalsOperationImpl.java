package com.zibobo.yedis.protocol.unified.hashes;

import com.zibobo.yedis.ops.BytesListReplyCallback;
import com.zibobo.yedis.ops.hashes.HvalsOperation;
import com.zibobo.yedis.protocol.unified.BytesListReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;

public class HvalsOperationImpl extends BytesListReplyOperationImpl implements
        HvalsOperation {
    private static final FixedArgsCommand CMD =
            new FixedArgsCommand("HVALS", 1);
    private final byte[] key;

    public HvalsOperationImpl(byte[] key, BytesListReplyCallback cb) {
        super(cb);
        this.key = key;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key);
    }

}
