package com.zibobo.yedis.protocol.unified.lists;

import com.zibobo.yedis.ops.BytesReplyCallback;
import com.zibobo.yedis.ops.lists.RpoplpushOperation;
import com.zibobo.yedis.protocol.unified.BytesReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;

public class RpoplpushOperationImpl extends BytesReplyOperationImpl implements
        RpoplpushOperation {
    private static final FixedArgsCommand CMD = new FixedArgsCommand(
            "RPOPLPUSH", 2);
    private final byte[] source;
    private final byte[] destination;

    public RpoplpushOperationImpl(byte[] source, byte[] destination,
            BytesReplyCallback cb) {
        super(cb);
        this.source = source;
        this.destination = destination;
    }

    @Override
    public void initialize() {
        setArguments(CMD, source, destination);
    }

}
