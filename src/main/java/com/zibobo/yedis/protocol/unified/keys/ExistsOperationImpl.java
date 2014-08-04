package com.zibobo.yedis.protocol.unified.keys;

import com.zibobo.yedis.ops.BooleanReplyCallback;
import com.zibobo.yedis.ops.keys.ExistsOperation;
import com.zibobo.yedis.protocol.unified.BooleanReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;

public class ExistsOperationImpl extends BooleanReplyOperationImpl implements
        ExistsOperation {

    private static final FixedArgsCommand CMD = new FixedArgsCommand("EXISTS",
            1);

    private final byte[] key;

    public ExistsOperationImpl(byte[] key, BooleanReplyCallback cb) {
        super(cb);
        this.key = key;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key);
    }

}
