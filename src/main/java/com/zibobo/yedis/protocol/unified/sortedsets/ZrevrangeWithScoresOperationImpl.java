package com.zibobo.yedis.protocol.unified.sortedsets;

import com.zibobo.yedis.ops.BytesSetWithScoresReplyCallback;
import com.zibobo.yedis.ops.sortedsets.ZrevrangeWithScoresOperation;
import com.zibobo.yedis.protocol.unified.BytesSetWithScoresReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.util.LongToBytesUtils;

public class ZrevrangeWithScoresOperationImpl extends
        BytesSetWithScoresReplyOperationImpl implements
        ZrevrangeWithScoresOperation {
    private static final byte[] WITHSCORES = toBytes("WITHSCORES");
    private static final FixedArgsCommand CMD = new FixedArgsCommand("ZREVRANGE",
            4);
    private final byte[] key;
    private final long start;
    private final long stop;

    public ZrevrangeWithScoresOperationImpl(byte[] key, long start, long stop,
            BytesSetWithScoresReplyCallback cb) {
        super(cb);
        this.key = key;
        this.start = start;
        this.stop = stop;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key, LongToBytesUtils.toBytes(start),
                LongToBytesUtils.toBytes(stop), WITHSCORES);

    }

}
