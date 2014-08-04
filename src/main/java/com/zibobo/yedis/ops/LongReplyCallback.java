package com.zibobo.yedis.ops;

public interface LongReplyCallback extends OperationCallback {

    public void onReply(long reply);
}
