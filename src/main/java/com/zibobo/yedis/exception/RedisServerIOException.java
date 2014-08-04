package com.zibobo.yedis.exception;

public class RedisServerIOException extends RedisException {

    private static final long serialVersionUID = 5508950873280958113L;

    public RedisServerIOException() {
        super();
    }

    public RedisServerIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public RedisServerIOException(String message) {
        super(message);
    }

    public RedisServerIOException(Throwable cause) {
        super(cause);
    }

}
