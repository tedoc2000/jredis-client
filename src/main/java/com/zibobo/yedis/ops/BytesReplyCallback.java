package com.zibobo.yedis.ops;

public interface BytesReplyCallback extends OperationCallback {

    public void onReply(byte[] reply);
}
