package com.zibobo.yedis.protocol.unified;

import com.zibobo.yedis.ops.StringReplyCallback;

public abstract class StringStatusReplyOperationImpl extends
        StatusReplyOperationImpl {

    private final StringReplyCallback cb;

    public StringStatusReplyOperationImpl(StringReplyCallback cb) {
        super(cb);
        this.cb = cb;
    }

    @Override
    protected void handleStatus(String status) {
        cb.onReply(status);
    }

}
