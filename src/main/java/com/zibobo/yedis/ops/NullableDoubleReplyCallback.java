package com.zibobo.yedis.ops;

public interface NullableDoubleReplyCallback extends OperationCallback {

    public void onReply(double reply);
    public void onNull();
}
