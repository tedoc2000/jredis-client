package com.zibobo.yedis.protocol.unified.strings;

import com.zibobo.yedis.ops.LongReplyCallback;
import com.zibobo.yedis.ops.strings.DecrOperation;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.protocol.unified.LongReplyOperationImpl;

public class DecrOperationImpl extends LongReplyOperationImpl implements
        DecrOperation {

    private final static FixedArgsCommand CMD = new FixedArgsCommand("DECR", 1);

    private final byte[] key;

    public DecrOperationImpl(byte[] key, LongReplyCallback cb) {
        super(cb);
        this.key = key;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key);

    }

}
