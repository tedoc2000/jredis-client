package com.zibobo.yedis.protocol.unified.strings;

import com.zibobo.yedis.ops.BytesReplyCallback;
import com.zibobo.yedis.ops.strings.GetOperation;
import com.zibobo.yedis.protocol.unified.ArgsCommand;
import com.zibobo.yedis.protocol.unified.BytesReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;

public class GetOperationImpl extends BytesReplyOperationImpl implements
        GetOperation {
    private final ArgsCommand cmd = new FixedArgsCommand("GET", 1);

    private final byte[] key;

    public GetOperationImpl(byte[] key, BytesReplyCallback cb) {
        super(cb);
        this.key = key;

    }

    @Override
    public void initialize() {
        setArguments(cmd, key);
    }

}
