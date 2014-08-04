package com.zibobo.yedis.ops;

public interface NullableLongReplyCallback extends OperationCallback {

    public void onReply(long reply);
    public void onNull();
}
