package com.zibobo.yedis.protocol.unified.keys;

import com.zibobo.yedis.ops.BooleanReplyCallback;
import com.zibobo.yedis.ops.keys.MoveOperation;
import com.zibobo.yedis.protocol.unified.BooleanReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.util.IntegerToBytesUtils;

public class MoveOperationImpl extends BooleanReplyOperationImpl implements
        MoveOperation {

    private static final FixedArgsCommand CMD = new FixedArgsCommand("MOVE", 2);

    private final byte[] key;
    private final int db;

    public MoveOperationImpl(byte[] key, int db, BooleanReplyCallback cb) {
        super(cb);
        this.key = key;
        this.db = db;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key, IntegerToBytesUtils.toBytes(db));
    }

}
