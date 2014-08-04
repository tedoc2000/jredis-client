package com.zibobo.yedis.protocol.unified.lists;

import com.zibobo.yedis.ops.BytesReplyCallback;
import com.zibobo.yedis.ops.lists.LindexOperation;
import com.zibobo.yedis.protocol.unified.BytesReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.util.LongToBytesUtils;

public class LindexOperationImpl extends BytesReplyOperationImpl implements
        LindexOperation {
    private static final FixedArgsCommand CMD = new FixedArgsCommand("LINDEX",
            2);
    private final byte[] key;
    private final long index;

    public LindexOperationImpl(byte[] key, long index, BytesReplyCallback cb) {
        super(cb);
        this.key = key;
        this.index = index;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key, LongToBytesUtils.toBytes(index));
    }

}
