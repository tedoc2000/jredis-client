package com.zibobo.yedis;

public class BlockingPopResult<T> {

    private final String key;
    private final T element;

    public BlockingPopResult(String key, T element) {
        this.key = key;
        this.element = element;
    }

    public String getKey() {
        return key;
    }

    public T getElement() {
        return element;
    }

}
