package com.zibobo.yedis.protocol.unified.strings;

import com.zibobo.yedis.ops.LongReplyCallback;
import com.zibobo.yedis.ops.strings.DecrOperation;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.protocol.unified.LongReplyOperationImpl;
import com.zibobo.yedis.util.LongToBytesUtils;

public class DecrByOperationImpl extends LongReplyOperationImpl implements
        DecrOperation {

    private final static FixedArgsCommand CMD = new FixedArgsCommand("DECRBY",
            2);

    private final byte[] key;
    private final long by;

    public DecrByOperationImpl(byte[] key, long by, LongReplyCallback cb) {
        super(cb);
        this.key = key;
        this.by = by;

    }

    @Override
    public void initialize() {
        setArguments(CMD, key, LongToBytesUtils.toBytes(by));

    }

}
