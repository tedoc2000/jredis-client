package com.zibobo.yedis.protocol.unified;

import com.zibobo.yedis.ops.DoubleReplyCallback;

public abstract class DoubleReplyOperationImpl extends BulkReplyOperationImpl {

    private final DoubleReplyCallback cb;

    public DoubleReplyOperationImpl(DoubleReplyCallback cb) {
        super(cb);
        this.cb = cb;
    }

    @Override
    protected void handleData(byte[] data) {
        cb.onReply(Double.parseDouble(new String(data, CHARSET)));
    }

}
