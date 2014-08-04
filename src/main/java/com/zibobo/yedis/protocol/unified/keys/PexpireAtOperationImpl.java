package com.zibobo.yedis.protocol.unified.keys;

import com.zibobo.yedis.ops.BooleanReplyCallback;
import com.zibobo.yedis.ops.keys.PexpireAtOperation;
import com.zibobo.yedis.protocol.unified.BooleanReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.util.LongToBytesUtils;

public class PexpireAtOperationImpl extends BooleanReplyOperationImpl implements
        PexpireAtOperation {

    private static final FixedArgsCommand CMD = new FixedArgsCommand("PEXPIREAT",
            2);

    private final byte[] key;
    private final long timestamp;

    public PexpireAtOperationImpl(byte[] key, long timestamp,
            BooleanReplyCallback cb) {
        super(cb);
        this.key = key;
        this.timestamp = timestamp;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key, LongToBytesUtils.toBytes(timestamp));
    }

}
