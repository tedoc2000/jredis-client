package com.zibobo.yedis.protocol.unified.hashes;

import com.zibobo.yedis.ops.BytesListReplyCallback;
import com.zibobo.yedis.ops.hashes.HmgetOperation;
import com.zibobo.yedis.protocol.unified.BytesListReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.VarArgsCommand;

public class HmgetOperationImpl extends BytesListReplyOperationImpl implements
        HmgetOperation {

    private static final VarArgsCommand CMD = new VarArgsCommand("HMGET");
    private final byte[] key;
    private final byte[][] fields;

    public HmgetOperationImpl(byte[] key, byte[][] fields,
            BytesListReplyCallback cb) {
        super(cb);
        this.key = key;
        this.fields = fields;
    }

    @Override
    public void initialize() {
        setVargsArguments(CMD, fields, key);
    }

}
