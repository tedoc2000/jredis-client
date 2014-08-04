package com.zibobo.yedis.protocol.unified.lists;

import java.util.List;

import com.zibobo.yedis.exception.RedisServerException;
import com.zibobo.yedis.ops.BlockingPopReplyCallback;
import com.zibobo.yedis.protocol.unified.MultiBulkReplyOperationImpl;
import com.zibobo.yedis.protocol.unified.response.BulkReply;
import com.zibobo.yedis.protocol.unified.response.Reply;

public abstract class BlockingPopReplyOperationImpl extends
        MultiBulkReplyOperationImpl {
    private final BlockingPopReplyCallback cb;

    public BlockingPopReplyOperationImpl(BlockingPopReplyCallback cb) {
        super(cb);
        this.cb = cb;
    }

    @Override
    protected void handleData(List<Reply<?>> replies) {
        if (replies == null) {
            cb.onTimeout();
        } else {
            if (replies.size() != 2) {
                throw new RedisServerException("Invalid server response");
            }
            String key =
                    new String(((BulkReply) replies.get(0)).reply, CHARSET);
            cb.onReply(key, ((BulkReply) replies.get(1)).reply);
        }
    }

}
