package com.zibobo.yedis.protocol.unified;

import com.zibobo.yedis.ops.OperationCallback;

public abstract class IgnoreStatusReplyOperationImpl extends
        StatusReplyOperationImpl {

    public IgnoreStatusReplyOperationImpl(OperationCallback cb) {
        super(cb);
    }

    @Override
    protected void handleStatus(String status) {

    }

}
