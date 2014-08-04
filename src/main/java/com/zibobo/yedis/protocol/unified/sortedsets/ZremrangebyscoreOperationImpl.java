package com.zibobo.yedis.protocol.unified.sortedsets;

import com.zibobo.yedis.ops.LongReplyCallback;
import com.zibobo.yedis.ops.sortedsets.ZremrangebyscoreOperation;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.protocol.unified.LongReplyOperationImpl;
import com.zibobo.yedis.util.DoubleToBytesUtils;

public class ZremrangebyscoreOperationImpl extends LongReplyOperationImpl
        implements ZremrangebyscoreOperation {

    private final FixedArgsCommand CMD = new FixedArgsCommand(
            "ZREMRANGEBYSCORE", 3);
    private final byte[] key;
    private final double min;
    private final double max;

    public ZremrangebyscoreOperationImpl(byte[] key, double min, double max,
            LongReplyCallback cb) {
        super(cb);
        this.key = key;
        this.min = min;
        this.max = max;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key, DoubleToBytesUtils.toBytes(min),
                DoubleToBytesUtils.toBytes(max));
    }

}
