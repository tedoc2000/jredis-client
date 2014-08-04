package com.zibobo.yedis.protocol.unified.sortedsets;

import com.zibobo.yedis.ops.BytesSetWithScoresReplyCallback;
import com.zibobo.yedis.ops.sortedsets.ZrangeWithScoresOperation;
import com.zibobo.yedis.protocol.unified.BytesSetWithScoresReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.util.LongToBytesUtils;

public class ZrangeWithScoresOperationImpl extends
        BytesSetWithScoresReplyOperationImpl implements
        ZrangeWithScoresOperation {
    private static final byte[] WITHSCORES = toBytes("WITHSCORES");
    private static final FixedArgsCommand CMD = new FixedArgsCommand("ZRANGE",
            4);
    private final byte[] key;
    private final long start;
    private final long stop;

    public ZrangeWithScoresOperationImpl(byte[] key, long start, long stop,
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
