package com.zibobo.yedis.protocol.unified.hashes;

import com.zibobo.yedis.ops.IntegerReplyCallback;
import com.zibobo.yedis.ops.hashes.HdelOperation;
import com.zibobo.yedis.protocol.unified.IntReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.VarArgsCommand;

public class HdelOperationImpl extends IntReplyOperationImpl implements
        HdelOperation {
    private static final VarArgsCommand CMD = new VarArgsCommand("HDEL");
    private final byte[] key;
    private final byte[][] fields;

    public HdelOperationImpl(byte[] key, byte[][] fields,
            IntegerReplyCallback cb) {
        super(cb);
        this.key = key;
        this.fields = fields;
    }

    @Override
    public void initialize() {
        setVargsArguments(CMD, fields, key);
    }

}
