package com.zibobo.yedis.exception;

public abstract class RedisException extends Error {

    private static final long serialVersionUID = 3302003544433294150L;

    public RedisException() {
        super();

    }

    public RedisException(String message, Throwable cause) {
        super(message, cause);
    }

    public RedisException(String message) {
        super(message);
    }

    public RedisException(Throwable cause) {
        super(cause);
    }

}
