package com.zibobo.yedis.protocol.unified.lists;

import com.zibobo.yedis.ops.LongReplyCallback;
import com.zibobo.yedis.ops.lists.LlenOperation;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.protocol.unified.LongReplyOperationImpl;

public class LlenOperationImpl extends LongReplyOperationImpl implements
        LlenOperation {
    private static final FixedArgsCommand CMD = new FixedArgsCommand("LLEN", 1);
    private final byte[] key;

    public LlenOperationImpl(byte[] key, LongReplyCallback cb) {
        super(cb);
        this.key = key;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key);
    }

}
