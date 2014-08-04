package com.zibobo.yedis.protocol.unified.lists;

import com.zibobo.yedis.ops.BytesReplyCallback;
import com.zibobo.yedis.ops.lists.RpopOperation;
import com.zibobo.yedis.protocol.unified.BytesReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;

public class RpopOperationImpl extends BytesReplyOperationImpl implements
        RpopOperation {
    private static final FixedArgsCommand CMD = new FixedArgsCommand("RPOP", 1);
    private final byte[] key;

    public RpopOperationImpl(byte[] key, BytesReplyCallback cb) {
        super(cb);
        this.key = key;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key);
    }

}
