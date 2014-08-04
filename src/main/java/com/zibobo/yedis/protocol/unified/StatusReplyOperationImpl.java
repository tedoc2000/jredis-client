package com.zibobo.yedis.protocol.unified;

import com.zibobo.yedis.ops.OperationCallback;
import com.zibobo.yedis.protocol.unified.response.StatusReply;

public abstract class StatusReplyOperationImpl extends OperationImpl {

    protected StatusReplyOperationImpl(OperationCallback cb) {
        super(cb);
    }

    @Override
    protected void handleStatusReply(StatusReply reply) {
        handleStatus(reply.reply);
    }

    protected abstract void handleStatus(String status);
}
