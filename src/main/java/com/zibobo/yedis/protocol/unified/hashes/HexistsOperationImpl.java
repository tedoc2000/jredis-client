package com.zibobo.yedis.protocol.unified.hashes;

import com.zibobo.yedis.ops.BooleanReplyCallback;
import com.zibobo.yedis.ops.hashes.HexistsOperation;
import com.zibobo.yedis.protocol.unified.BooleanReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;

public class HexistsOperationImpl extends BooleanReplyOperationImpl implements
        HexistsOperation {
    private static final FixedArgsCommand CMD = new FixedArgsCommand("HEXISTS",
            2);
    private final byte[] key;
    private final byte[] field;

    public HexistsOperationImpl(byte[] key, byte[] field,
            BooleanReplyCallback cb) {
        super(cb);
        this.key = key;
        this.field = field;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key, field);
    }

}
