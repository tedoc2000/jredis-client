package com.zibobo.yedis.protocol.unified.sortedsets;

import com.zibobo.yedis.ops.DoubleReplyCallback;
import com.zibobo.yedis.ops.sortedsets.ZincrbyOperation;
import com.zibobo.yedis.protocol.unified.DoubleReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.util.DoubleToBytesUtils;

public class ZincrbyOperationImpl extends DoubleReplyOperationImpl implements
        ZincrbyOperation {
    private static final FixedArgsCommand CMD = new FixedArgsCommand("ZINCRBY",
            3);
    private final byte[] key;
    private final double increment;
    private final byte[] member;

    public ZincrbyOperationImpl(byte[] key, double increment, byte[] member,
            DoubleReplyCallback cb) {
        super(cb);
        this.key = key;
        this.increment = increment;
        this.member = member;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key, DoubleToBytesUtils.toBytes(increment), member);
    }

}
