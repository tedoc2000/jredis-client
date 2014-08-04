package com.zibobo.yedis.protocol.unified.response;

public interface ReplyParser<T extends Reply<?>> extends Parser {

    public T getReply();
}
