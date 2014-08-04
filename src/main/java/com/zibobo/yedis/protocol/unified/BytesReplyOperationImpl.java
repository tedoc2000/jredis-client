package com.zibobo.yedis.protocol.unified;

import com.zibobo.yedis.ops.BytesReplyCallback;

public abstract class BytesReplyOperationImpl extends BulkReplyOperationImpl {

    private final BytesReplyCallback cb;

    public BytesReplyOperationImpl(BytesReplyCallback cb) {
        super(cb);
        this.cb = cb;
    }

    @Override
    protected void handleData(byte[] data) {
        cb.onReply(data);
    }

}
