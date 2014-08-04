package com.zibobo.yedis.protocol.unified;

import com.zibobo.yedis.ops.IntegerReplyCallback;

public abstract class IntReplyOperationImpl extends IntegerReplyOperationImpl {

    private final IntegerReplyCallback cb;

    public IntReplyOperationImpl(IntegerReplyCallback cb) {
        super(cb);
        this.cb = cb;
    }

    @Override
    protected void handleData(long results) {
        cb.onReply((int) results);
    }

}
