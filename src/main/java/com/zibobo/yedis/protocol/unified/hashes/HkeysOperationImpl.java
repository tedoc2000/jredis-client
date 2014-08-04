package com.zibobo.yedis.protocol.unified.hashes;

import com.zibobo.yedis.ops.StringListReplyCallback;
import com.zibobo.yedis.ops.hashes.HkeysOperation;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.protocol.unified.StringListReplyOperationImpl;

public class HkeysOperationImpl extends StringListReplyOperationImpl implements
        HkeysOperation {
    private static final FixedArgsCommand CMD =
            new FixedArgsCommand("HKEYS", 1);
    private final byte[] key;

    public HkeysOperationImpl(byte[] key, StringListReplyCallback cb) {
        super(cb);
        this.key = key;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key);
    }

}
