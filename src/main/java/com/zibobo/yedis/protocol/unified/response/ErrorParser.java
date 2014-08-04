package com.zibobo.yedis.protocol.unified.response;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import com.zibobo.yedis.exception.RedisException;
import com.zibobo.yedis.exception.RedisServerException;
import com.zibobo.yedis.exception.RedisServerIOException;
import com.zibobo.yedis.exception.RedisWrongKeyTypeException;

public class ErrorParser extends AbstractReplyParser<ErrorReply> {

    protected static final Charset CHARSET = Charset.forName("UTF-8");
    private boolean foundCr = false;
    private final ByteArrayOutputStream errorStream =
            new ByteArrayOutputStream();

    public ErrorParser() {
        super(new ErrorReply());
    }

    @Override
    public void parse(ByteBuffer data) {
        boolean done = false;
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
                errorStream.write(b);
            }
        }
        if (done) {
            String line = new String(errorStream.toByteArray(), CHARSET);
            errorStream.reset();
            handleError(line);
        }
    }

    protected void handleError(String line) throws RedisException {
        getLogger().debug("Error:  %s", line);
        if (line.startsWith("ERR ")) {
            reply.reply = new RedisServerException(line.substring(4));
        } else if (line.startsWith("WRONGTYPE ")) {
            reply.reply = new RedisWrongKeyTypeException(line.substring(10));
        } else if (line.startsWith("IOERR ")) {
            reply.reply = new RedisServerIOException(line.substring(7));
        } else {
            reply.reply = new RedisServerException(line);
        }
        done = true;
    }

}
