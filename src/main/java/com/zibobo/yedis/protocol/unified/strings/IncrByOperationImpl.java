package com.zibobo.yedis.protocol.unified.strings;

import com.zibobo.yedis.ops.LongReplyCallback;
import com.zibobo.yedis.ops.strings.IncrOperation;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.protocol.unified.LongReplyOperationImpl;
import com.zibobo.yedis.util.LongToBytesUtils;

public class IncrByOperationImpl extends LongReplyOperationImpl implements
        IncrOperation {

    private final static FixedArgsCommand CMD = new FixedArgsCommand("INCRBY",
            2);

    private final byte[] key;
    private final long by;

    public IncrByOperationImpl(byte[] key, long by,LongReplyCallback cb) {
        super(cb);
        this.key = key;
        this.by = by;

    }

    @Override
    public void initialize() {
        setArguments(CMD, key, LongToBytesUtils.toBytes(by));
    }

}
