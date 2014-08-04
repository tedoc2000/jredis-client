package com.zibobo.yedis.exception;

public class RedisServerException extends RedisException {

    private static final long serialVersionUID = -3612770632972186652L;

    public RedisServerException() {
        super();
    }

    public RedisServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public RedisServerException(String message) {
        super(message);
    }

    public RedisServerException(Throwable cause) {
        super(cause);
    }

}
