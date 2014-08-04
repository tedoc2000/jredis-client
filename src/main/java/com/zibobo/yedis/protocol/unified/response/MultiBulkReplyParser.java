package com.zibobo.yedis.protocol.unified.response;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class MultiBulkReplyParser extends AbstractReplyParser<MultiBulkReply> {

    private Parser parser = new IntegerParser();
    private long remainingReplies;

    public MultiBulkReplyParser() {
        super(new MultiBulkReply());
    }

    @Override
    public void parse(ByteBuffer data) {
        parser.parse(data);
        if (parser.done()) {
            if (parser instanceof IntegerParser) {
                remainingReplies = ((IntegerParser) parser).getResults();
                if (remainingReplies == 0) {
                    done = true;
                    return;
                } else {
                    reply.reply = new ArrayList<Reply<?>>();
                    parser = new AnyReplyParser();
                }
            } else {
                ReplyParser<?> repParser = (ReplyParser<?>) parser;
                reply.reply.add(repParser.getReply());
                remainingReplies--;
                if (remainingReplies != 0) {
                    parser = new AnyReplyParser();
                } else {
                    done = true;
                }
            }
        }

    }

}
