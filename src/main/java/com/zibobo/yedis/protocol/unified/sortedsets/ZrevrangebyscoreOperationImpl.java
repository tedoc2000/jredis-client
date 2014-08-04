package com.zibobo.yedis.protocol.unified.sortedsets;

import com.zibobo.yedis.IntervalValue;
import com.zibobo.yedis.ops.BytesSetReplyCallback;
import com.zibobo.yedis.ops.sortedsets.ZrevrangebyscoreOperation;
import com.zibobo.yedis.protocol.unified.BytesSetReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.OptionalArgsCommand;
import com.zibobo.yedis.util.LongToBytesUtils;

public class ZrevrangebyscoreOperationImpl extends BytesSetReplyOperationImpl
        implements ZrevrangebyscoreOperation {

    private static final OptionalArgsCommand CMD = new OptionalArgsCommand(
            "ZREVRANGEBYSCORE");

    private static final byte[] LIMIT = toBytes("LIMIT");
    private final byte[] key;
    private final IntervalValue min;
    private final IntervalValue max;
    private final Long limit;
    private final Long count;

    public ZrevrangebyscoreOperationImpl(byte[] key, IntervalValue min,
            IntervalValue max, Long limit, Long count, BytesSetReplyCallback cb) {
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
            setArguments(CMD, key, min.toBytes(), max.toBytes(), LIMIT,
                    LongToBytesUtils.toBytes(limit),
                    LongToBytesUtils.toBytes(count));
        } else {
            setArguments(CMD, key, min.toBytes(), max.toBytes());
        }
    }

}
