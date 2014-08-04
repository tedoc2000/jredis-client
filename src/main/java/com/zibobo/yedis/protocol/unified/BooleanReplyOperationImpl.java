package com.zibobo.yedis.protocol.unified;

import com.zibobo.yedis.ops.BooleanReplyCallback;

public abstract class BooleanReplyOperationImpl extends
        IntegerReplyOperationImpl {

    private final BooleanReplyCallback cb;

    public BooleanReplyOperationImpl(BooleanReplyCallback cb) {
        super(cb);
        this.cb = cb;
    }

    @Override
    protected void handleData(long results) {
        cb.onReply(results == 1);
    }

}
