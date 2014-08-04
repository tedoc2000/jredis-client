package com.zibobo.yedis.protocol.unified;

import com.zibobo.yedis.ops.BooleanReplyCallback;

public abstract class OKBooleanReplyOperationImpl extends
        StatusReplyOperationImpl {

    private final BooleanReplyCallback cb;

    public OKBooleanReplyOperationImpl(BooleanReplyCallback cb) {
        super(cb);
        this.cb = cb;
    }

    @Override
    protected void handleStatus(String results) {
        cb.onReply("OK".equals(results));
    }

}
