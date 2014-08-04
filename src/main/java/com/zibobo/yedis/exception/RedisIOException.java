package com.zibobo.yedis.exception;

public class RedisIOException extends RedisException {

    private static final long serialVersionUID = -7673011489056682565L;

    public RedisIOException() {
        super();
    }

    public RedisIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public RedisIOException(String message) {
        super(message);
    }

    public RedisIOException(Throwable cause) {
        super(cause);
    }

}
