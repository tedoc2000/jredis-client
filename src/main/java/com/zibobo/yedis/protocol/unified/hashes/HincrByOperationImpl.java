package com.zibobo.yedis.protocol.unified.hashes;

import com.zibobo.yedis.ops.LongReplyCallback;
import com.zibobo.yedis.ops.hashes.HincrOperation;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.protocol.unified.LongReplyOperationImpl;
import com.zibobo.yedis.util.LongToBytesUtils;

public class HincrByOperationImpl extends LongReplyOperationImpl implements
        HincrOperation {

    private final static FixedArgsCommand CMD = new FixedArgsCommand("HINCRBY",
            3);

    private final byte[] key;
    private final byte[] field;
    private final long by;

    public HincrByOperationImpl(byte[] key, byte[] field, long by,
            LongReplyCallback cb) {
        super(cb);
        this.key = key;
        this.field = field;
        this.by = by;

    }

    @Override
    public void initialize() {
        setArguments(CMD, key, field, LongToBytesUtils.toBytes(by));
    }

}
