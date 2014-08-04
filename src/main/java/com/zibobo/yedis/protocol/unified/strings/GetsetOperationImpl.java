package com.zibobo.yedis.protocol.unified.strings;

import com.zibobo.yedis.ops.BytesReplyCallback;
import com.zibobo.yedis.ops.strings.GetsetOperation;
import com.zibobo.yedis.protocol.unified.BytesReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;

public class GetsetOperationImpl extends BytesReplyOperationImpl implements
        GetsetOperation {

    private static final FixedArgsCommand CMD = new FixedArgsCommand("GETSET",
            2);

    private final byte[] key;
    private final byte[] value;

    public GetsetOperationImpl(byte[] key, byte[] value, BytesReplyCallback cb) {
        super(cb);
        this.key = key;
        this.value = value;

    }

    @Override
    public void initialize() {
        setArguments(CMD, key, value);
    }

}
