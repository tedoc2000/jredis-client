package com.zibobo.yedis.protocol.unified.strings;

import com.zibobo.yedis.ops.LongReplyCallback;
import com.zibobo.yedis.ops.strings.IncrOperation;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.protocol.unified.LongReplyOperationImpl;

public class IncrOperationImpl extends LongReplyOperationImpl implements
        IncrOperation {

    private final static FixedArgsCommand CMD = new FixedArgsCommand("INCR", 1);

    private final byte[] key;

    public IncrOperationImpl(byte[] key, LongReplyCallback cb) {
        super(cb);
        this.key = key;

    }

    @Override
    public void initialize() {
        setArguments(CMD, key);

    }

}
