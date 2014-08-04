package com.zibobo.yedis.protocol.unified.lists;

import com.zibobo.yedis.ops.BytesReplyCallback;
import com.zibobo.yedis.ops.lists.BrpoplpushOperation;
import com.zibobo.yedis.protocol.unified.FixedArgsCommand;
import com.zibobo.yedis.protocol.unified.OperationImpl;
import com.zibobo.yedis.protocol.unified.response.BulkReply;
import com.zibobo.yedis.protocol.unified.response.MultiBulkReply;
import com.zibobo.yedis.util.IntegerToBytesUtils;

public class BrpoplpushOperatonImpl extends OperationImpl implements
        BrpoplpushOperation {

    private static final FixedArgsCommand CMD = new FixedArgsCommand(
            "BRPOPLPUSH", 3);
    private final byte[] source;
    private final byte[] destination;
    private final int timeout;
    private final BytesReplyCallback cb;

    public BrpoplpushOperatonImpl(byte[] source, byte[] destination,
            int timeout, BytesReplyCallback cb) {
        super(cb);
        this.source = source;
        this.destination = destination;
        this.timeout = timeout;
        this.cb = cb;
    }

    @Override
    public void initialize() {
        setArguments(CMD, source, destination,
                IntegerToBytesUtils.toBytes(timeout));
    }

    @Override
    protected void handleBulkReply(BulkReply reply) {
        cb.onReply(reply.reply);
    }

    @Override
    protected void handleMultiBulkReply(MultiBulkReply reply) {
        cb.onReply(null);
    }

}
