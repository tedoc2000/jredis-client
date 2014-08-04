package com.zibobo.yedis.protocol.unified.response;

import java.nio.ByteBuffer;

public class BulkReplyParser extends AbstractReplyParser<BulkReply> {

    private Parser parser = new IntegerParser();

    public BulkReplyParser() {
        super(new BulkReply());
    }

    @Override
    public void parse(ByteBuffer data) {
        parser.parse(data);
        if (parser.done()) {
            if (parser instanceof IntegerParser) {
                long length = ((IntegerParser) parser).getResults();
                if (length == -1) {
                    done = true;
                } else {
                    parser = new ByteParser((int) length);
                }
            } else {
                done = true;
                reply.reply = ((ByteParser) parser).getResults();
            }
        }

    }
}
