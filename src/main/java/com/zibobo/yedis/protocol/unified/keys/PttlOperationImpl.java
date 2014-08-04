package com.zibobo.yedis.protocol.unified.keys;

import com.zibobo.yedis.ops.LongReplyCallback;
import com.zibobo.yedis.ops.keys.PttlOperation;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.protocol.unified.LongReplyOperationImpl;

public class PttlOperationImpl extends LongReplyOperationImpl implements
        PttlOperation {

    private static final FixedArgsCommand CMD = new FixedArgsCommand("PTTL", 1);
    private final byte[] key;

    public PttlOperationImpl(byte[] key, LongReplyCallback cb) {
        super(cb);
        this.key = key;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key);
    }

}
