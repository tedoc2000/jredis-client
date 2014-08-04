package com.zibobo.yedis.protocol.unified.sortedsets;

import com.zibobo.yedis.ops.LongReplyCallback;
import com.zibobo.yedis.ops.sortedsets.ZcountOperation;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.protocol.unified.LongReplyOperationImpl;
import com.zibobo.yedis.util.DoubleToBytesUtils;

public class ZcountOperationImpl extends LongReplyOperationImpl implements
        ZcountOperation {

    private static final FixedArgsCommand CMD = new FixedArgsCommand("ZCOUNT",
            3);
    private final byte[] key;
    private final double min;
    private final double max;

    public ZcountOperationImpl(byte[] key, double min, double max,
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
