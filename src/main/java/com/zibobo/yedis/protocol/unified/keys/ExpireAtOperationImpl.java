package com.zibobo.yedis.protocol.unified.keys;

import com.zibobo.yedis.ops.BooleanReplyCallback;
import com.zibobo.yedis.ops.keys.ExpireAtOperation;
import com.zibobo.yedis.protocol.unified.BooleanReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.util.IntegerToBytesUtils;

public class ExpireAtOperationImpl extends BooleanReplyOperationImpl implements
        ExpireAtOperation {

    private static final FixedArgsCommand CMD = new FixedArgsCommand("EXPIREAT",
            2);

    private final byte[] key;
    private final int timestamp;

    public ExpireAtOperationImpl(byte[] key, int timestamp,
            BooleanReplyCallback cb) {
        super(cb);
        this.key = key;
        this.timestamp = timestamp;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key, IntegerToBytesUtils.toBytes(timestamp));
    }

}
