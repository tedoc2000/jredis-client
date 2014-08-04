package com.zibobo.yedis.protocol.unified.lists;

import com.zibobo.yedis.ops.LongReplyCallback;
import com.zibobo.yedis.ops.lists.LremOperation;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.protocol.unified.LongReplyOperationImpl;
import com.zibobo.yedis.util.LongToBytesUtils;

public class LremOperationImpl extends LongReplyOperationImpl implements
        LremOperation {
    private static final FixedArgsCommand CMD = new FixedArgsCommand("LREM", 3);
    private final byte[] key;
    private final long count;
    private final byte[] value;

    public LremOperationImpl(byte[] key, long count, byte[] value,
            LongReplyCallback cb) {
        super(cb);
        this.key = key;
        this.count = count;
        this.value = value;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key, LongToBytesUtils.toBytes(count), value);
    }

}
