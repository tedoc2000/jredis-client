package com.zibobo.yedis.protocol.unified;

import com.zibobo.yedis.ops.StringReplyCallback;

public abstract class StringReplyOperationImpl extends BulkReplyOperationImpl {

    private final StringReplyCallback cb;

    public StringReplyOperationImpl(StringReplyCallback cb) {
        super(cb);
        this.cb = cb;
    }

    @Override
    protected void handleData(byte[] data) {
        cb.onReply(new String(data, CHARSET));
    }

}
