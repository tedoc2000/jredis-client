/**
 * Copyright (C) 2006-2009 Dustin Sallings
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALING
 * IN THE SOFTWARE.
 */

package com.zibobo.yedis.protocol.unified;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import com.zibobo.yedis.exception.RedisException;
import com.zibobo.yedis.exception.RedisIOException;
import com.zibobo.yedis.ops.Operation;
import com.zibobo.yedis.ops.OperationCallback;
import com.zibobo.yedis.ops.OperationState;
import com.zibobo.yedis.ops.OperationStatus;
import com.zibobo.yedis.protocol.BaseOperationImpl;
import com.zibobo.yedis.protocol.unified.response.AnyReplyParser;
import com.zibobo.yedis.protocol.unified.response.BulkReply;
import com.zibobo.yedis.protocol.unified.response.ErrorReply;
import com.zibobo.yedis.protocol.unified.response.IntegerReply;
import com.zibobo.yedis.protocol.unified.response.MultiBulkReply;
import com.zibobo.yedis.protocol.unified.response.Reply;
import com.zibobo.yedis.protocol.unified.response.StatusReply;
import com.zibobo.yedis.util.IntegerToBytesUtils;

/**
 * Operations on a redis connection.
 */
public abstract class OperationImpl extends
        BaseOperationImpl implements Operation {

    protected static final OperationStatus END = new OperationStatus(true,
            "END");
    protected static final Charset CHARSET = Charset.forName("UTF-8");

    protected static final byte[] toBytes(String s) {
        return s.getBytes(CHARSET);
    }

    protected static final byte[] CRLF = { '\r', '\n' };

    protected final AnyReplyParser parser = new AnyReplyParser();

    protected OperationImpl(OperationCallback cb) {
        super();
        callback = cb;
    }

    @Override
    public void readFromBuffer(ByteBuffer data) throws RedisException {

        while (getState() != OperationState.COMPLETE && data.remaining() > 0) {
            parser.parse(data);
            if (parser.done()) {
                Reply<?> reply = parser.getReply();
                if (reply instanceof ErrorReply) {
                    ErrorReply error = (ErrorReply) reply;
                    exception = error.reply;
                    callback.receivedStatus(new OperationStatus(false,
                            exception.getMessage()));
                    transitionState(OperationState.COMPLETE);
                    return;
                }
                if (reply instanceof BulkReply) {
                    handleBulkReply((BulkReply) reply);
                } else if (reply instanceof StatusReply) {
                    handleStatusReply((StatusReply) reply);
                } else if (reply instanceof IntegerReply) {
                    handleIntegerReply((IntegerReply) reply);
                } else if (reply instanceof MultiBulkReply) {
                    handleMultiBulkReply((MultiBulkReply) reply);
                }
                callback.receivedStatus(END);
                transitionState(OperationState.COMPLETE);
            }
        }
    }

    protected void setArguments(ArgsCommand cmd, byte[]... args) {

        int size =
                cmd.getArgCountSize(args) + 3 + cmd.getCmdLengthSize() + 3
                        + cmd.getCmdSize() + 2;
        boolean haveArgs = args != null && args.length != 0;
        if (haveArgs) {
            for (byte[] arg : args) {
                // +3 for the $ and \r\n
                size += IntegerToBytesUtils.bytesSize(arg.length) + 3;
                // +2 for the \r\n
                size += arg.length + 2;
            }
        }

        ByteBuffer b = ByteBuffer.allocate(size);
        b.put((byte) '*').put(cmd.getArgsCountBytes(args)).put(CRLF);
        b.put((byte) '$').put(cmd.getCmdLengthBytes()).put(CRLF);
        b.put(cmd.getCmdBytes()).put(CRLF);

        if (haveArgs) {
            for (byte[] arg : args) {
                b.put((byte) '$').put(IntegerToBytesUtils.toBytes(arg.length))
                        .put(CRLF);
                b.put(arg).put(CRLF);
            }
        }
        b.flip();
        setBuffer(b);
    }

    protected void setVargsArguments(VarArgsCommand cmd, byte[][] vargs,
            byte[]... args) {

        int size =
                cmd.getArgCountSize(vargs, args) + 3 + cmd.getCmdLengthSize()
                        + 3 + cmd.getCmdSize() + 2;
        boolean haveArgs = args != null && args.length != 0;
        if (haveArgs) {
            for (byte[] arg : args) {
                // +3 for the $ and \r\n
                size += IntegerToBytesUtils.bytesSize(arg.length) + 3;
                // +2 for the \r\n
                size += arg.length + 2;
            }
        }
        for (byte[] varg : vargs) {
            // +3 for the $ and \r\n
            size += IntegerToBytesUtils.bytesSize(varg.length) + 3;
            // +2 for the \r\n
            size += varg.length + 2;
        }

        ByteBuffer b = ByteBuffer.allocate(size);
        b.put((byte) '*').put(cmd.getArgsCountBytes(vargs, args)).put(CRLF);
        b.put((byte) '$').put(cmd.getCmdLengthBytes()).put(CRLF);
        b.put(cmd.getCmdBytes()).put(CRLF);

        if (haveArgs) {
            for (byte[] arg : args) {
                b.put((byte) '$').put(IntegerToBytesUtils.toBytes(arg.length))
                        .put(CRLF);
                b.put(arg).put(CRLF);
            }
        }
        for (byte[] varg : vargs) {
            b.put((byte) '$').put(IntegerToBytesUtils.toBytes(varg.length))
                    .put(CRLF);
            b.put(varg).put(CRLF);
        }
        b.flip();
        setBuffer(b);
    }

    protected void setVargsPostArgsArguments(VarArgsCommand cmd,
            byte[][] vargs, byte[]... args) {

        int size =
                cmd.getArgCountSize(vargs, args) + 3 + cmd.getCmdLengthSize()
                        + 3 + cmd.getCmdSize() + 2;

        for (byte[] varg : vargs) {
            // +3 for the $ and \r\n
            size += IntegerToBytesUtils.bytesSize(varg.length) + 3;
            // +2 for the \r\n
            size += varg.length + 2;
        }
        boolean haveArgs = args != null && args.length != 0;
        if (haveArgs) {
            for (byte[] arg : args) {
                // +3 for the $ and \r\n
                size += IntegerToBytesUtils.bytesSize(arg.length) + 3;
                // +2 for the \r\n
                size += arg.length + 2;
            }
        }
        ByteBuffer b = ByteBuffer.allocate(size);
        b.put((byte) '*').put(cmd.getArgsCountBytes(vargs, args)).put(CRLF);
        b.put((byte) '$').put(cmd.getCmdLengthBytes()).put(CRLF);
        b.put(cmd.getCmdBytes()).put(CRLF);
        for (byte[] varg : vargs) {
            b.put((byte) '$').put(IntegerToBytesUtils.toBytes(varg.length))
                    .put(CRLF);
            b.put(varg).put(CRLF);
        }
        if (haveArgs) {
            for (byte[] arg : args) {
                b.put((byte) '$').put(IntegerToBytesUtils.toBytes(arg.length))
                        .put(CRLF);
                b.put(arg).put(CRLF);
            }
        }
        b.flip();
        setBuffer(b);
    }

    protected void handleBulkReply(BulkReply reply) {
        throw new RedisIOException("Wrong type returned by server");
    }

    protected void handleStatusReply(StatusReply reply) {
        throw new RedisIOException("Wrong type returned by server");
    }

    protected void handleIntegerReply(IntegerReply reply) {
        throw new RedisIOException("Wrong type returned by server");
    }

    protected void handleMultiBulkReply(MultiBulkReply reply) {
        throw new RedisIOException("Wrong type returned by server");
    }
}
