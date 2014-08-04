package com.zibobo.yedis.ops;

public interface BooleanReplyCallback extends OperationCallback {

    public void onReply(boolean reply);
}
