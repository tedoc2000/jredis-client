package com.zibobo.yedis.protocol.unified;

import java.util.List;

import com.zibobo.yedis.ops.BytesSetWithScoresReplyCallback;
import com.zibobo.yedis.protocol.unified.response.BulkReply;
import com.zibobo.yedis.protocol.unified.response.Reply;

public abstract class BytesSetWithScoresReplyOperationImpl extends
        MultiBulkReplyOperationImpl {

    protected final BytesSetWithScoresReplyCallback cb;

    public BytesSetWithScoresReplyOperationImpl(BytesSetWithScoresReplyCallback cb) {
        super(cb);
        this.cb = cb;
    }

    @Override
    protected void handleData(List<Reply<?>> replies) {
        if (replies == null) {
            cb.onEmptySet();
        } else {
            cb.onSize(replies.size()/2);
            boolean expectingScore = false;
            byte[] data = null;
            for (Reply<?> reply : replies) {
                if (expectingScore) {
                    cb.onData(Double.valueOf(new String(((BulkReply)reply).reply, CHARSET)), data);
                    expectingScore = false;
                } else {
                    data = ((BulkReply) reply).reply;
                    expectingScore = true;
                }
            }
        }
    }

}
