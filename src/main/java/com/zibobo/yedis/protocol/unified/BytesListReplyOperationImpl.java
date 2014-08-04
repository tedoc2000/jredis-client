package com.zibobo.yedis.protocol.unified;

import java.util.List;

import com.zibobo.yedis.ops.BytesListReplyCallback;
import com.zibobo.yedis.protocol.unified.response.BulkReply;
import com.zibobo.yedis.protocol.unified.response.Reply;

public abstract class BytesListReplyOperationImpl extends
        MultiBulkReplyOperationImpl {

    protected final BytesListReplyCallback cb;

    public BytesListReplyOperationImpl(BytesListReplyCallback cb) {
        super(cb);
        this.cb = cb;
    }

    @Override
    protected void handleData(List<Reply<?>> replies) {
        if (replies == null) {
            cb.onEmptyList();
        } else {
            cb.onSize(replies.size());
            for (Reply<?> reply : replies) {
                cb.onData(((BulkReply) reply).reply);
            }
        }
    }

}
