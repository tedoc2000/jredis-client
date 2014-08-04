package com.zibobo.yedis.ops;

public interface StringListReplyCallback extends OperationCallback {
    /**
     * Called if the list is defined
     *
     * @param size
     *            the size of full list
     */
    public void onSize(int size);

    /**
     * Called in order, once per item in the list
     *
     * @param data
     *            possibly null data
     */
    public void onData(String data);

    /**
     * Called if the list is empty
     */
    public void onEmptyList();
}
