package com.zibobo.yedis.protocol.unified.response;

public abstract class AbstractReplyParser<T extends Reply<?>> extends
        AbstractParser implements ReplyParser<T> {

    protected final T reply;

    public AbstractReplyParser(T reply) {
        super();
        this.reply = reply;
    }

    @Override
    public T getReply() {
        return reply;
    }

}
