package com.zibobo.yedis.protocol.unified.lists;

import com.zibobo.yedis.ops.LongReplyCallback;
import com.zibobo.yedis.ops.lists.LpushOperation;
import com.zibobo.yedis.protocol.unified.LongReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.VarArgsCommand;

public class LpushOperationImpl extends LongReplyOperationImpl implements
        LpushOperation {
    private static final VarArgsCommand CMD = new VarArgsCommand("LPUSH");
    private final byte[] key;
    private final byte[][] values;

    public LpushOperationImpl(byte[] key, byte[][] values, LongReplyCallback cb) {
        super(cb);
        this.key = key;
        this.values = values;
    }

    @Override
    public void initialize() {
        setVargsArguments(CMD, values, key);
    }

}
