package com.zibobo.yedis.protocol.unified.sets;

import com.zibobo.yedis.ops.BytesReplyCallback;
import com.zibobo.yedis.ops.sets.SpopOperation;
import com.zibobo.yedis.protocol.unified.ArgsCommand;
import com.zibobo.yedis.protocol.unified.BytesReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;

public class SpopOperationImpl extends BytesReplyOperationImpl implements
        SpopOperation {

    private final ArgsCommand CMD = new FixedArgsCommand("SPOP", 1);
    private final byte[] key;

    public SpopOperationImpl(byte[] key, BytesReplyCallback cb) {
        super(cb);
        this.key = key;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key);
    }

}
