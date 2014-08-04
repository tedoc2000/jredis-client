package com.zibobo.yedis.protocol.unified.keys;

import com.zibobo.yedis.ops.BooleanReplyCallback;
import com.zibobo.yedis.ops.keys.RenamenxOperation;
import com.zibobo.yedis.protocol.unified.BooleanReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;

public class RenamenxOperationImpl extends BooleanReplyOperationImpl implements
        RenamenxOperation {

    private static final FixedArgsCommand CMD = new FixedArgsCommand(
            "RENAMENX", 2);
    private final byte[] key;
    private final byte[] newKey;

    public RenamenxOperationImpl(byte[] key, byte[] newKey,
            BooleanReplyCallback cb) {
        super(cb);
        this.key = key;
        this.newKey = newKey;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key, newKey);
    }

}
