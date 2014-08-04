package com.zibobo.yedis.protocol.unified.response;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class StatusReplyParser extends AbstractReplyParser<StatusReply> {

    private static final Charset CHARSET = Charset.forName("UTF-8");
    private final ByteArrayOutputStream byteBuffer =
            new ByteArrayOutputStream();
    private boolean foundCr = false;

    public StatusReplyParser() {
        super(new StatusReply());
    }

    @Override
    public void parse(ByteBuffer data) {
        while (data.remaining() > 0) {
            byte b = data.get();
            if (b == '\r') {
                foundCr = true;
            } else if (b == '\n') {
                assert foundCr : "got a \\n without a \\r";
                done = true;
                break;
            } else {
                assert !foundCr : "got a \\r without a \\n";
                byteBuffer.write(b);
            }
        }
        if (done) {
            reply.reply = new String(byteBuffer.toByteArray(), CHARSET);
            byteBuffer.reset();
        }

    }

}
