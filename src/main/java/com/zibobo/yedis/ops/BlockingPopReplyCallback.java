package com.zibobo.yedis.ops;

public interface BlockingPopReplyCallback extends OperationCallback {

    /**
     * Called if the blocking pop timedout
     */
    public void onTimeout();

    /**
     * Called if the blocking pop succeeds
     *
     * @param key
     *            the key
     * @param data
     *            the data
     */
    public void onReply(String key, byte[] data);

}
