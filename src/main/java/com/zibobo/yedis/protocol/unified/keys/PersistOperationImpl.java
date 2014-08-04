package com.zibobo.yedis.protocol.unified.keys;

import com.zibobo.yedis.ops.BooleanReplyCallback;
import com.zibobo.yedis.ops.keys.PersistOperation;
import com.zibobo.yedis.protocol.unified.BooleanReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;

public class PersistOperationImpl extends BooleanReplyOperationImpl implements
        PersistOperation {

    private static final FixedArgsCommand CMD = new FixedArgsCommand("PERSIST",
            1);
    private final byte[] key;

    public PersistOperationImpl(byte[] key, BooleanReplyCallback cb) {
        super(cb);
        this.key = key;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key);
    }

}
