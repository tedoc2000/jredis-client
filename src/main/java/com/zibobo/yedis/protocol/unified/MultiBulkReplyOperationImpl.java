package com.zibobo.yedis.protocol.unified;

import java.util.List;

import com.zibobo.yedis.ops.OperationCallback;
import com.zibobo.yedis.protocol.unified.response.MultiBulkReply;
import com.zibobo.yedis.protocol.unified.response.Reply;

public abstract class MultiBulkReplyOperationImpl extends OperationImpl {

    protected MultiBulkReplyOperationImpl(OperationCallback cb) {
        super(cb);
    }

    @Override
    protected void handleMultiBulkReply(MultiBulkReply reply) {
        handleData(reply.reply);

    }

    abstract protected void handleData(List<Reply<?>> replies);

}
