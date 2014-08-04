package com.zibobo.yedis.protocol.unified.sortedsets;

import com.zibobo.yedis.IntervalValue;
import com.zibobo.yedis.ops.BytesSetWithScoresReplyCallback;
import com.zibobo.yedis.ops.sortedsets.ZrangebyscoreWithScoresOperation;
import com.zibobo.yedis.protocol.unified.BytesSetWithScoresReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.OptionalArgsCommand;
import com.zibobo.yedis.util.LongToBytesUtils;

public class ZrangebyscoreWithScoresOperationImpl extends
        BytesSetWithScoresReplyOperationImpl implements ZrangebyscoreWithScoresOperation {

    private static final OptionalArgsCommand CMD = new OptionalArgsCommand(
            "ZRANGEBYSCORE");

    private static final byte[] LIMIT = toBytes("LIMIT");
    private static final byte[] WITHSCORES = toBytes("WITHSCORES");
    private final byte[] key;
    private final IntervalValue min;
    private final IntervalValue max;
    private final Long limit;
    private final Long count;

    public ZrangebyscoreWithScoresOperationImpl(byte[] key,
            IntervalValue min, IntervalValue max, Long limit, Long count,
            BytesSetWithScoresReplyCallback cb) {
        super(cb);
        this.key = key;
        this.min = min;
        this.max = max;
        this.limit = limit;
        this.count = count;
    }

    @Override
    public void initialize() {
        if (limit != null) {
            setArguments(CMD, key, min.toBytes(), max.toBytes(), WITHSCORES,
                    LIMIT, LongToBytesUtils.toBytes(limit),
                    LongToBytesUtils.toBytes(count));
        } else {
            setArguments(CMD, key, min.toBytes(), max.toBytes(), WITHSCORES);
        }
    }

}
