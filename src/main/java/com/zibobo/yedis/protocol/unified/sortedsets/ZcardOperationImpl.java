package com.zibobo.yedis.protocol.unified.sortedsets;

import com.zibobo.yedis.ops.LongReplyCallback;
import com.zibobo.yedis.ops.sortedsets.ZcardOperation;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.protocol.unified.LongReplyOperationImpl;

public class ZcardOperationImpl extends LongReplyOperationImpl implements
        ZcardOperation {

    private static final FixedArgsCommand CMD =
            new FixedArgsCommand("ZCARD", 1);
    private final byte[] key;

    public ZcardOperationImpl(byte[] key, LongReplyCallback cb) {
        super(cb);
        this.key = key;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key);
    }

}
