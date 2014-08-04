package com.zibobo.yedis.protocol.unified;

import java.util.List;

import com.zibobo.yedis.ops.StringListReplyCallback;
import com.zibobo.yedis.protocol.unified.response.BulkReply;
import com.zibobo.yedis.protocol.unified.response.Reply;

public abstract class StringListReplyOperationImpl extends
        MultiBulkReplyOperationImpl {

    protected final StringListReplyCallback cb;

    public StringListReplyOperationImpl(StringListReplyCallback cb) {
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
                cb.onData(new String(((BulkReply) reply).reply, CHARSET));
            }
        }
    }

}
