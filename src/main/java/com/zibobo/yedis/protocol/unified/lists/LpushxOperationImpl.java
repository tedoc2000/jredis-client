package com.zibobo.yedis.protocol.unified.lists;

import com.zibobo.yedis.ops.LongReplyCallback;
import com.zibobo.yedis.ops.lists.LpushxOperation;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.protocol.unified.LongReplyOperationImpl;

public class LpushxOperationImpl extends LongReplyOperationImpl implements
        LpushxOperation {
    private static final FixedArgsCommand CMD = new FixedArgsCommand("LPUSHX",
            2);
    private final byte[] key;
    private final byte[] value;

    public LpushxOperationImpl(byte[] key, byte[] value, LongReplyCallback cb) {
        super(cb);
        this.key = key;
        this.value = value;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key, value);
    }

}
