package com.zibobo.yedis.exception;

public class RedisWrongKeyTypeException extends RedisException {

    private static final long serialVersionUID = 3130848079862070380L;

    public RedisWrongKeyTypeException() {
        super();
    }

    public RedisWrongKeyTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public RedisWrongKeyTypeException(String message) {
        super(message);
    }

    public RedisWrongKeyTypeException(Throwable cause) {
        super(cause);
    }

}
