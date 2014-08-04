package com.zibobo.yedis.protocol.unified.lists;

import com.zibobo.yedis.ops.LongReplyCallback;
import com.zibobo.yedis.ops.lists.RpushxOperation;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.protocol.unified.LongReplyOperationImpl;

public class RpushxOperationImpl extends LongReplyOperationImpl implements
        RpushxOperation {
    private static final FixedArgsCommand CMD = new FixedArgsCommand("RPUSHX",
            2);
    private final byte[] key;
    private final byte[] value;

    public RpushxOperationImpl(byte[] key, byte[] value, LongReplyCallback cb) {
        super(cb);
        this.key = key;
        this.value = value;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key, value);
    }

}
