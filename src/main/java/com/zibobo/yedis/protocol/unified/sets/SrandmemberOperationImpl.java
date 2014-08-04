package com.zibobo.yedis.protocol.unified.sets;

import com.zibobo.yedis.ops.BytesReplyCallback;
import com.zibobo.yedis.ops.sets.SrandmemberOperation;
import com.zibobo.yedis.protocol.unified.ArgsCommand;
import com.zibobo.yedis.protocol.unified.BytesReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;

public class SrandmemberOperationImpl extends BytesReplyOperationImpl implements
        SrandmemberOperation {

    private final ArgsCommand CMD = new FixedArgsCommand("SRANDMEMBER", 1);
    private final byte[] key;

    public SrandmemberOperationImpl(byte[] key, BytesReplyCallback cb) {
        super(cb);
        this.key = key;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key);
    }

}
