package com.zibobo.yedis.protocol.unified.keys;

import com.zibobo.yedis.ops.BooleanReplyCallback;
import com.zibobo.yedis.ops.keys.ExpireOperation;
import com.zibobo.yedis.protocol.unified.BooleanReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.util.IntegerToBytesUtils;

public class ExpireOperationImpl extends BooleanReplyOperationImpl implements
        ExpireOperation {

    private static final FixedArgsCommand CMD = new FixedArgsCommand("EXPIRE",
            2);

    private final byte[] key;
    private final int seconds;

    public ExpireOperationImpl(byte[] key, int seconds, BooleanReplyCallback cb) {
        super(cb);
        this.key = key;
        this.seconds = seconds;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key, IntegerToBytesUtils.toBytes(seconds));
    }

}
