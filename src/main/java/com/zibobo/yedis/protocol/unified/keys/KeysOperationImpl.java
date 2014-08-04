package com.zibobo.yedis.protocol.unified.keys;

import com.zibobo.yedis.ops.BytesSetReplyCallback;
import com.zibobo.yedis.ops.keys.KeysOperation;
import com.zibobo.yedis.protocol.unified.BytesSetReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;

public class KeysOperationImpl extends BytesSetReplyOperationImpl implements
        KeysOperation {
    private static final FixedArgsCommand CMD = new FixedArgsCommand("KEYS", 1);

    private final byte[] pattern;

    public KeysOperationImpl(byte[] pattern, BytesSetReplyCallback cb) {
        super(cb);
        this.pattern = pattern;
    }

    @Override
    public void initialize() {
        setArguments(CMD, pattern);
    }

}
