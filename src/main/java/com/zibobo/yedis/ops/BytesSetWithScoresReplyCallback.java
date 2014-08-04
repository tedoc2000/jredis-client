package com.zibobo.yedis.ops;

public interface BytesSetWithScoresReplyCallback extends OperationCallback {
    /**
     * Called if the set is defined
     *
     * @param size
     *            the size of full set
     */
    public void onSize(int size);

    /**
     * Called in order, once per item in the set
     *
     * @param data
     *            possibly null data
     */
    public void onData(double score, byte[] data);

    /**
     * Called if the set is empty
     */
    public void onEmptySet();
}
