package com.zibobo.yedis.protocol.unified.response;

import java.nio.ByteBuffer;

import net.spy.memcached.compat.SpyObject;

import com.zibobo.yedis.exception.RedisIOException;

public class AnyReplyParser extends SpyObject implements ReplyParser<Reply<?>> {

    private Reply<?> reply;
    private ReplyParser<?> parser;
    private boolean done;

    @Override
    public void parse(ByteBuffer data) {
        if (parser == null) {
            byte b = data.get();
            switch (b) {
            case '-':
                parser = new ErrorParser();
                break;
            case '+':
                parser = new StatusReplyParser();
                break;
            case ':':
                parser = new IntegerReplyParser();
                break;
            case '$':
                parser = new BulkReplyParser();
                break;
            case '*':
                parser = new MultiBulkReplyParser();
                break;
            default:
                throw new RedisIOException("Unexpected status response.");
            }
        }
        parser.parse(data);
        done = parser.done();
        if (done) {
            reply = parser.getReply();
        }
    }

    @Override
    public Reply<?> getReply() {
        return reply;
    }

    @Override
    public boolean done() {
        return done;
    }

}
