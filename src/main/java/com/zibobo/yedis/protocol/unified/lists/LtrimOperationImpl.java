package com.zibobo.yedis.protocol.unified.lists;

import com.zibobo.yedis.ops.OperationCallback;
import com.zibobo.yedis.ops.lists.LtrimOperation;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.protocol.unified.IgnoreStatusReplyOperationImpl;
import com.zibobo.yedis.util.LongToBytesUtils;

public class LtrimOperationImpl extends IgnoreStatusReplyOperationImpl
        implements LtrimOperation {
    private static final FixedArgsCommand CMD =
            new FixedArgsCommand("LTRIM", 3);
    private final byte[] key;
    private final long start;
    private final long stop;

    public LtrimOperationImpl(byte[] key, long start, long stop,
            OperationCallback cb) {
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
