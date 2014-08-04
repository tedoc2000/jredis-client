package com.zibobo.yedis.protocol.unified.sets;

import com.zibobo.yedis.ops.LongReplyCallback;
import com.zibobo.yedis.ops.sets.ScardOperation;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.protocol.unified.LongReplyOperationImpl;

public class ScardOperationImpl extends LongReplyOperationImpl implements
        ScardOperation {
    private static final FixedArgsCommand CMD =
            new FixedArgsCommand("SCARD", 1);
    private final byte[] key;

    public ScardOperationImpl(byte[] key, LongReplyCallback cb) {
        super(cb);
        this.key = key;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key);
    }

}
