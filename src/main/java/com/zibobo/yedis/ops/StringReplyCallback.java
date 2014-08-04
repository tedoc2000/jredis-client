package com.zibobo.yedis.ops;

public interface StringReplyCallback extends OperationCallback {

    public void onReply(String reply);
}
