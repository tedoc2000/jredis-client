package com.zibobo.yedis.protocol.unified.response;

import java.nio.ByteBuffer;

public class IntegerReplyParser extends AbstractReplyParser<IntegerReply> {

    private final IntegerParser parser = new IntegerParser();

    public IntegerReplyParser() {
        super(new IntegerReply());
    }

    @Override
    public void parse(ByteBuffer data) {
        parser.parse(data);
        if (parser.done()) {
            reply.reply = parser.getResults();
            done = true;
        }

    }

}
