package com.zibobo.yedis.protocol.unified.lists;

import com.zibobo.yedis.ops.BytesReplyCallback;
import com.zibobo.yedis.ops.lists.LpopOperation;
import com.zibobo.yedis.protocol.unified.BytesReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;

public class LpopOperationImpl extends BytesReplyOperationImpl implements
        LpopOperation {
    private static final FixedArgsCommand CMD = new FixedArgsCommand("LPOP", 1);
    private final byte[] key;

    public LpopOperationImpl(byte[] key, BytesReplyCallback cb) {
        super(cb);
        this.key = key;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key);
    }

}
