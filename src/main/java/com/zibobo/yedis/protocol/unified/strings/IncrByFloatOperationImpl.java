package com.zibobo.yedis.protocol.unified.strings;

import com.zibobo.yedis.ops.DoubleReplyCallback;
import com.zibobo.yedis.ops.strings.IncrByFloatOperation;
import com.zibobo.yedis.protocol.unified.DoubleReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.util.DoubleToBytesUtils;

public class IncrByFloatOperationImpl extends DoubleReplyOperationImpl
        implements IncrByFloatOperation {

    private final static FixedArgsCommand CMD = new FixedArgsCommand(
            "INCRBYFLOAT", 2);

    private final byte[] key;
    private final double by;

    public IncrByFloatOperationImpl(byte[] key, double by,
            DoubleReplyCallback cb) {
        super(cb);
        this.key = key;
        this.by = by;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key, DoubleToBytesUtils.toBytes(by));
    }

}
