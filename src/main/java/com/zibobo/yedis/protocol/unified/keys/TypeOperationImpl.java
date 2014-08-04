package com.zibobo.yedis.protocol.unified.keys;

import com.zibobo.yedis.ops.StringReplyCallback;
import com.zibobo.yedis.ops.keys.TypeOperation;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.protocol.unified.StringStatusReplyOperationImpl;

public class TypeOperationImpl extends StringStatusReplyOperationImpl implements
        TypeOperation {

    private static final FixedArgsCommand CMD = new FixedArgsCommand("TYPE", 1);
    private final byte[] key;

    public TypeOperationImpl(byte[] key, StringReplyCallback cb) {
        super(cb);
        this.key = key;
    }

    @Override
    public void initialize() {
        setArguments(CMD, key);
    }

}
