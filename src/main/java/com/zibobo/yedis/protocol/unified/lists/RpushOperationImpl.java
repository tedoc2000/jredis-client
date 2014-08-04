package com.zibobo.yedis.protocol.unified.lists;

import com.zibobo.yedis.ops.LongReplyCallback;
import com.zibobo.yedis.ops.lists.RpushOperation;
import com.zibobo.yedis.protocol.unified.LongReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.VarArgsCommand;

public class RpushOperationImpl extends LongReplyOperationImpl implements
        RpushOperation {
    private static final VarArgsCommand CMD = new VarArgsCommand("RPUSH");
    private final byte[] key;
    private final byte[][] values;

    public RpushOperationImpl(byte[] key, byte[][] values, LongReplyCallback cb) {
        super(cb);
        this.key = key;
        this.values = values;
    }

    @Override
    public void initialize() {
        setVargsArguments(CMD, values, key);
    }

}
