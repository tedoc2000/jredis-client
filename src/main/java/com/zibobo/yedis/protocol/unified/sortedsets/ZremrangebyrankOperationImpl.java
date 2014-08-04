package com.zibobo.yedis.protocol.unified.sortedsets;

import com.zibobo.yedis.ops.LongReplyCallback;
import com.zibobo.yedis.ops.sortedsets.ZremrangebyrankOperation;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.protocol.unified.LongReplyOperationImpl;
import com.zibobo.yedis.util.LongToBytesUtils;

public class ZremrangebyrankOperationImpl extends LongReplyOperationImpl
        implements ZremrangebyrankOperation {
    private final FixedArgsCommand CMD = new FixedArgsCommand(
            "ZREMRANGEBYRANK", 3);
    private final byte[] key;
    private final long start;
    private final long stop;

    public ZremrangebyrankOperationImpl(byte[] key, long start, long stop,
            LongReplyCallback cb) {
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
