package com.zibobo.yedis.protocol.unified;

import com.zibobo.yedis.ops.LongReplyCallback;

public abstract class LongReplyOperationImpl extends IntegerReplyOperationImpl {

    private final LongReplyCallback cb;

    public LongReplyOperationImpl(LongReplyCallback cb) {
        super(cb);
        this.cb = cb;
    }

    @Override
    protected void handleData(long results) {
        cb.onReply(results);
    }

}
