package com.zibobo.yedis.protocol.unified.hashes;

import com.zibobo.yedis.ops.DoubleReplyCallback;
import com.zibobo.yedis.ops.hashes.HincrByFloatOperation;
import com.zibobo.yedis.protocol.unified.DoubleReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.util.DoubleToBytesUtils;

public class HincrByFloatOperationImpl extends DoubleReplyOperationImpl
        implements HincrByFloatOperation {

    private final static FixedArgsCommand CMD = new FixedArgsCommand(
            "HINCRBYFLOAT", 3);

    private final byte[] key;
    private final byte[] field;
    private final double by;

    public HincrByFloatOperationImpl(byte[] key, byte[] field, double by,
            DoubleReplyCallback cb) {
        super(cb);
        this.key = key;
        this.field = field;
        this.by = by;

    }

    @Override
    public void initialize() {
        setArguments(CMD, key, field, DoubleToBytesUtils.toBytes(by));
    }

}
