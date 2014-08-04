package com.zibobo.yedis.protocol.unified;

import com.zibobo.yedis.ops.OperationCallback;
import com.zibobo.yedis.protocol.unified.response.IntegerReply;

public abstract class IntegerReplyOperationImpl extends OperationImpl {

    public IntegerReplyOperationImpl(OperationCallback cb) {
        super(cb);
    }

    @Override
    protected void handleIntegerReply(IntegerReply reply) {
        handleData(reply.reply);
    }

    protected abstract void handleData(long results);
}
