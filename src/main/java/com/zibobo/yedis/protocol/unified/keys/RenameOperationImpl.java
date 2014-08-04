package com.zibobo.yedis.protocol.unified.keys;

import com.zibobo.yedis.ops.BooleanReplyCallback;
import com.zibobo.yedis.ops.keys.RenameOperation;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.protocol.unified.OKBooleanReplyOperationImpl;

public class RenameOperationImpl extends OKBooleanReplyOperationImpl implements
        RenameOperation {

    private static final FixedArgsCommand CMD = new FixedArgsCommand("RENAME",
            2);
    private final byte[] key;
    private final byte[] newKey;

    public RenameOperationImpl(byte[] key, byte[] newKey,
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
