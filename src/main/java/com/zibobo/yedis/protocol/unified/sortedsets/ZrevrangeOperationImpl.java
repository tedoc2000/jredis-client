package com.zibobo.yedis.protocol.unified.sortedsets;

import com.zibobo.yedis.ops.BytesSetReplyCallback;
import com.zibobo.yedis.ops.sortedsets.ZrevrangeOperation;
import com.zibobo.yedis.protocol.unified.BytesSetReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.util.LongToBytesUtils;

public class ZrevrangeOperationImpl extends BytesSetReplyOperationImpl implements
        ZrevrangeOperation {

    private static final FixedArgsCommand CMD = new FixedArgsCommand("ZREVRANGE",
            3);

    private final byte[] key;
    private final long start;
    private final long stop;

    public ZrevrangeOperationImpl(byte[] key, long start, long stop,
            BytesSetReplyCallback cb) {
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
