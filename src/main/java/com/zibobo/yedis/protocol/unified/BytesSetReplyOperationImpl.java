package com.zibobo.yedis.protocol.unified;

import java.util.List;

import com.zibobo.yedis.ops.BytesSetReplyCallback;
import com.zibobo.yedis.protocol.unified.response.BulkReply;
import com.zibobo.yedis.protocol.unified.response.Reply;

public abstract class BytesSetReplyOperationImpl extends
        MultiBulkReplyOperationImpl {

    protected final BytesSetReplyCallback cb;

    public BytesSetReplyOperationImpl(BytesSetReplyCallback cb) {
        super(cb);
        this.cb = cb;
    }

    @Override
    protected void handleData(List<Reply<?>> replies) {
        if (replies == null) {
            cb.onEmptySet();
        } else {
            cb.onSize(replies.size());
            for (Reply<?> reply : replies) {
                cb.onData(((BulkReply) reply).reply);
            }
        }
    }

}
