package com.zibobo.yedis.protocol.unified.sets;

import com.zibobo.yedis.ops.BytesSetReplyCallback;
import com.zibobo.yedis.ops.sets.SmembersOperation;
import com.zibobo.yedis.protocol.unified.ArgsCommand;
import com.zibobo.yedis.protocol.unified.BytesSetReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;

public class SmembersOperationImpl extends BytesSetReplyOperationImpl implements
        SmembersOperation {

    private final ArgsCommand CMD = new FixedArgsCommand("SMEMBERS", 1);
    private final byte[] key;

    public SmembersOperationImpl(byte[] key, BytesSetReplyCallback cb) {
        super(cb);
        this.key = key;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key);
    }

}
