package com.zibobo.yedis.protocol.unified.sets;

import com.zibobo.yedis.ops.BooleanReplyCallback;
import com.zibobo.yedis.ops.sets.SismembersOperation;
import com.zibobo.yedis.protocol.unified.BooleanReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;

public class SismemberOperationImpl extends BooleanReplyOperationImpl implements
        SismembersOperation {

    private static final FixedArgsCommand CMD = new FixedArgsCommand(
            "SISMEMBER", 2);
    private final byte[] key;
    private final byte[] member;

    public SismemberOperationImpl(byte[] key, byte[] member,
            BooleanReplyCallback cb) {
        super(cb);
        this.key = key;
        this.member = member;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key, member);
    }

}
