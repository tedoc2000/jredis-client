package com.zibobo.yedis.protocol.unified.lists;

import com.zibobo.yedis.ops.LongReplyCallback;
import com.zibobo.yedis.ops.lists.LinsertOperation;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.protocol.unified.LongReplyOperationImpl;

public class LinsertOperationImpl extends LongReplyOperationImpl implements
        LinsertOperation {
    private static final FixedArgsCommand CMD = new FixedArgsCommand("LINSERT",
            4);
    private final byte[] key;
    private final byte[] position;
    private final byte[] pivot;
    private final byte[] value;

    public LinsertOperationImpl(byte[] key, byte[] position, byte[] pivot,
            byte[] value, LongReplyCallback cb) {
        super(cb);
        this.key = key;
        this.position = position;
        this.pivot = pivot;
        this.value = value;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key, position, pivot, value);
    }

}
