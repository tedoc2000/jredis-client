package com.zibobo.yedis.protocol.unified.lists;

import com.zibobo.yedis.ops.BytesListReplyCallback;
import com.zibobo.yedis.ops.lists.LrangeOperation;
import com.zibobo.yedis.protocol.unified.BytesListReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.util.LongToBytesUtils;

public class LrangeOperationImpl extends BytesListReplyOperationImpl implements
        LrangeOperation {
    private static final FixedArgsCommand CMD = new FixedArgsCommand("LRANGE",
            3);
    private final byte[] key;
    private final long start;
    private final long stop;

    public LrangeOperationImpl(byte[] key, long start, long stop,
            BytesListReplyCallback cb) {
        super(cb);
        this.key = key;
        this.start = start;
        this.stop = stop;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key, LongToBytesUtils.toBytes(start),
                LongToBytesUtils.toBytes(stop));
    }

}
