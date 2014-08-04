package com.zibobo.yedis.ops;

public interface IntegerReplyCallback extends OperationCallback {

    public void onReply(int reply);
}
