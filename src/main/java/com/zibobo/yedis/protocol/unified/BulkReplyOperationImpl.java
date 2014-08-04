package com.zibobo.yedis.protocol.unified;

import com.zibobo.yedis.ops.OperationCallback;
import com.zibobo.yedis.protocol.unified.response.BulkReply;

public abstract class BulkReplyOperationImpl extends OperationImpl {

    protected BulkReplyOperationImpl(OperationCallback cb) {
        super(cb);
    }

    @Override
    protected void handleBulkReply(BulkReply reply) {
        handleData(reply.reply);
    }

    protected abstract void handleData(byte[] data);

}
